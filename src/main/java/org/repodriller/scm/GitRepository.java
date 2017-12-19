/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.repodriller.scm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.repodriller.RepoDrillerException;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Developer;
import org.repodriller.domain.Modification;
import org.repodriller.domain.ModificationType;
import org.repodriller.util.RDFileUtils;

/**
 * Everything you need to work with a Git-based source code repository.
 *
 *
 * @author Mauricio Aniche
 */
/* TODO Name: Sounds like it inherits SCMRepository, but it actually implements SCM. */
public class GitRepository implements SCM {

	/* Constants. */
	private static final int MAX_SIZE_OF_A_DIFF = 100000;
	private static final int DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT = 5000;
	private static final String BRANCH_MM = "mm"; /* TODO mm -> rd. */

	/* Auto-determined. */
	private String mainBranchName = null;
	private int maxNumberFilesInACommit = -1; /* TODO Expose an API to control this value? Also in SubversionRepository. */
	private int maxSizeOfDiff = -1; /* TODO Expose an API to control this value? Also in SubversionRepository. */

	private CollectConfiguration collectConfig;

	private static Logger log = LogManager.getLogger(GitRepository.class);

	/* User-specified. */
	private String path = null;
	private boolean firstParentOnly = false;

	/**
	 * Intended for sub-classes.
	 * Make sure you initialize appropriately with the Setters.
	 */
	protected GitRepository() {
		this(null);
	}

	public GitRepository(String path) {
		this(path, false);
	}


	public GitRepository(String path, boolean firstParentOnly) {
		log.debug("Creating a GitRepository from path " + path);
		setPath(path);
		setFirstParentOnly(firstParentOnly);

		maxNumberFilesInACommit = checkMaxNumberOfFiles();
		maxSizeOfDiff = checkMaxSizeOfDiff();

		this.collectConfig = new CollectConfiguration().everything();
	}

	public static SCMRepository singleProject(String path) {
		return new GitRepository(path).info();
	}

	public static SCMRepository singleProject(String path, boolean singleParentOnly) {
		return new GitRepository(path, singleParentOnly).info();
	}

	public static SCMRepository[] allProjectsIn(String path) {
		return allProjectsIn(path, false);
	}

	public static SCMRepository[] allProjectsIn(String path, boolean singleParentOnly) {
		List<SCMRepository> repos = new ArrayList<>();

		for (String dir : RDFileUtils.getAllDirsIn(path)) {
			repos.add(singleProject(dir, singleParentOnly));
		}

		return repos.toArray(new SCMRepository[repos.size()]);
	}

	public SCMRepository info() {
		try (Git git = openRepository(); RevWalk rw = new RevWalk(git.getRepository())) {
            AnyObjectId headId = git.getRepository().resolve(Constants.HEAD);

			RevCommit root = rw.parseCommit(headId);
			rw.sort(RevSort.REVERSE);
			rw.markStart(root);
			RevCommit lastCommit = rw.next();

			String origin = git.getRepository().getConfig().getString("remote", "origin", "url");

			return new SCMRepository(this, origin, path, headId.getName(), lastCommit.getName());
		} catch (Exception e) {
			throw new RuntimeException("Couldn't create JGit instance with path " + path);
		}
	}

	protected Git openRepository() throws IOException, GitAPIException {
		Git git = Git.open(new File(path));
		if (this.mainBranchName == null) {
			this.mainBranchName = discoverMainBranchName(git);
		}
		return git;
	}

	private String discoverMainBranchName(Git git) throws IOException {
		return git.getRepository().getBranch();
	}

	public ChangeSet getHead() {
		RevWalk revWalk = null;
        try (Git git = openRepository()) {
			ObjectId head = git.getRepository().resolve(Constants.HEAD);

			revWalk = new RevWalk(git.getRepository());
			RevCommit r = revWalk.parseCommit(head);
			return new ChangeSet(r.getName(), convertToDate(r));

		} catch (Exception e) {
			throw new RuntimeException("error in getHead() for " + path, e);
		} finally {
			revWalk.close();
		}
	}

	@Override
	public List<ChangeSet> getChangeSets() {
        try (Git git = openRepository()) {
            List<ChangeSet> allCs;
			if (!firstParentOnly) allCs = getAllCommits(git);
			else allCs = firstParentsOnly(git);

			return allCs;
		} catch (Exception e) {
            throw new RuntimeException("error in getChangeSets for " + path, e);
        }
	}

	private List<ChangeSet> firstParentsOnly(Git git) {
		RevWalk revWalk = null;
		try {
			List<ChangeSet> allCs = new ArrayList<>();

			revWalk = new RevWalk(git.getRepository());
			revWalk.setRevFilter(new FirstParentFilter());
			revWalk.sort(RevSort.TOPO);
			Ref headRef = git.getRepository().getRef(Constants.HEAD);  /* TODO Deprecated. */
			RevCommit headCommit = revWalk.parseCommit(headRef.getObjectId());
			revWalk.markStart( headCommit );
			for(RevCommit revCommit : revWalk) {
				allCs.add(extractChangeSet(revCommit));
			}

			return allCs;

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			revWalk.close();
		}
	}

	private List<ChangeSet> getAllCommits(Git git) throws GitAPIException, IOException {
		List<ChangeSet> allCs = new ArrayList<>();

		for (RevCommit r : git.log().all().call()) {
			allCs.add(extractChangeSet(r));
		}
		return allCs;
	}

	private ChangeSet extractChangeSet(RevCommit r) {
		String hash = r.getName();
		GregorianCalendar date = convertToDate(r);

		return new ChangeSet(hash, date);
	}

	private GregorianCalendar convertToDate(RevCommit revCommit) {
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeZone(revCommit.getAuthorIdent().getTimeZone());
		date.setTime(revCommit.getAuthorIdent().getWhen());

		return date;
	}

	/**
	 * Get the commit with this commit id.
	 * Caveats:
	 *   - If commit modifies more than maxNumberFilesInACommit, throws an exception
	 *   - If one of the file diffs exceeds maxSizeOfDiff, the diffText is discarded
	 *
	 * @param id	The SHA1 hash that identifies a git commit.
	 * @returns Commit	The corresponding Commit, or null.
	 */
	@Override
	public Commit getCommit(String id) {
		try (Git git = openRepository()) {
			/* Using JGit, this commit will be the first entry in the log beginning at id. */
			Repository repo = git.getRepository();
			Iterable<RevCommit> jgitCommits = git.log().add(repo.resolve(id)).call();
			Iterator<RevCommit> itr = jgitCommits.iterator();

			if (!itr.hasNext())
				return null;
			RevCommit jgitCommit = itr.next();

			/* Extract metadata. */
			Developer author = new Developer(jgitCommit.getAuthorIdent().getName(), jgitCommit.getAuthorIdent().getEmailAddress());
			Developer committer = new Developer(jgitCommit.getCommitterIdent().getName(), jgitCommit.getCommitterIdent().getEmailAddress());
			TimeZone authorTimeZone = jgitCommit.getAuthorIdent().getTimeZone();
			TimeZone committerTimeZone = jgitCommit.getCommitterIdent().getTimeZone();

			String msg = collectConfig.isCollectingCommitMessages() ? jgitCommit.getFullMessage().trim() : "";
			String hash = jgitCommit.getName().toString();
			List<String> parents = Arrays.stream(jgitCommit.getParents())
					.map(rc -> rc.getName().toString()).collect(Collectors.toList());

			GregorianCalendar authorDate = new GregorianCalendar();
			authorDate.setTime(jgitCommit.getAuthorIdent().getWhen());
			authorDate.setTimeZone(jgitCommit.getAuthorIdent().getTimeZone());

			GregorianCalendar committerDate = new GregorianCalendar();
			committerDate.setTime(jgitCommit.getCommitterIdent().getWhen());
			committerDate.setTimeZone(jgitCommit.getCommitterIdent().getTimeZone());

			boolean isMerge = (jgitCommit.getParentCount() > 1);

			Set<String> branches = getBranches(git, hash);
			boolean isCommitInMainBranch = branches.contains(this.mainBranchName);

			/* Create one of our Commit's based on the jgitCommit metadata. */
			Commit commit = new Commit(hash, author, committer, authorDate, authorTimeZone, committerDate, committerTimeZone, msg, parents, isMerge, branches, isCommitInMainBranch);

			/* Convert each of the associated DiffEntry's to a Modification. */
			List<DiffEntry> diffsForTheCommit = diffsForTheCommit(repo, jgitCommit);
			if (diffsForTheCommit.size() > maxNumberFilesInACommit) {
				String errMsg = "Commit " + id + " touches more than " + maxNumberFilesInACommit + " files";
				log.error(errMsg);
				throw new RepoDrillerException(errMsg);
			}

			for (DiffEntry diff : diffsForTheCommit) {
				Modification m = this.diffToModification(repo, diff);
				commit.addModification(m);
			}

			return commit;
		} catch (Exception e) {
			throw new RuntimeException("error detailing " + id + " in " + path, e);
		}
	}

	private Set<String> getBranches(Git git, String hash) throws GitAPIException {

		if(!collectConfig.isCollectingBranches())
			return new HashSet<>();

		List<Ref> gitBranches = git.branchList().setContains(hash).call();
		Set<String> mappedBranches = gitBranches.stream()
				.map(
					(ref) -> ref.getName().substring(ref.getName().lastIndexOf("/")+1))
				.collect(Collectors.toSet());
		return mappedBranches;
	}

	private Modification diffToModification(Repository repo, DiffEntry diff) throws IOException {
		ModificationType change = Enum.valueOf(ModificationType.class, diff.getChangeType().toString());

		String oldPath = diff.getOldPath();
		String newPath = diff.getNewPath();

		String diffText = "";
		String sc = "";
		if (diff.getChangeType() != ChangeType.DELETE) {
			diffText = getDiffText(repo, diff);
			sc = getSourceCode(repo, diff);
		}

		if (diffText.length() > maxSizeOfDiff) {
			log.error("diff for " + newPath + " too big");
			diffText = "-- TOO BIG --";
		}

		return new Modification(oldPath, newPath, change, diffText, sc);
	}

	private List<DiffEntry> diffsForTheCommit(Repository repo, RevCommit commit) throws IOException {

		AnyObjectId currentCommit = repo.resolve(commit.getName());
		AnyObjectId parentCommit = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null;

		return this.getDiffBetweenCommits(repo, parentCommit, currentCommit);
	}

	@Override
	public List<Modification> getDiffBetweenCommits(String priorCommitHash, String laterCommitHash) {
		try (Git git = openRepository()) {
			Repository repo = git.getRepository();
			AnyObjectId priorCommit = repo.resolve(priorCommitHash);
			AnyObjectId laterCommit = repo.resolve(laterCommitHash);

			List<DiffEntry> diffs = this.getDiffBetweenCommits(repo, priorCommit, laterCommit);
			List<Modification> modifications = diffs.stream()
				.map(diff -> {
					try {
						return this.diffToModification(repo, diff);
					} catch (IOException e) {
						throw new RuntimeException("error diffing " + priorCommitHash + " and " + laterCommitHash + " in " + path, e);
					}
				})
				.collect(Collectors.toList());
			return modifications;
		} catch (Exception e) {
			throw new RuntimeException("error diffing " + priorCommitHash + " and " + laterCommitHash + " in " + path, e);
		}
	}


	private List<DiffEntry> getDiffBetweenCommits(Repository repo, AnyObjectId parentCommit, AnyObjectId currentCommit) {
		try (DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE)) {

            df.setBinaryFileThreshold(2 * 1024); // 2 mb max a file
            df.setRepository(repo);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            setContext(df);

            List<DiffEntry> diffs = null;

            if (parentCommit == null) {
                try(RevWalk rw = new RevWalk(repo)) {
                		RevCommit commit = rw.parseCommit(currentCommit);
                    diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree()));
                }
            } else {
                diffs = df.scan(parentCommit, currentCommit);
            }
            return diffs;
        } catch (IOException e) {
        	throw new RuntimeException("error diffing " + parentCommit.getName() + " and " + currentCommit.getName() + " in " + path, e);
		}
	}

	private void setContext(DiffFormatter df) {
		try {
			int context = getSystemProperty("git.diffcontext"); /* TODO: make it into a configuration */
			df.setContext(context);
		} catch (Exception e) {
			return;
		}
	}

	private String getSourceCode(Repository repo, DiffEntry diff) throws IOException {

		if(!collectConfig.isCollectingSourceCode()) return "";

		try {
			ObjectReader reader = repo.newObjectReader();
			byte[] bytes = reader.open(diff.getNewId().toObjectId()).getBytes();
			return new String(bytes, "utf-8");
		} catch (Throwable e) {
			return "";
		}
	}

	private String getDiffText(Repository repo, DiffEntry diff) throws IOException {

		if(!collectConfig.isCollectingDiffs())
			return "";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DiffFormatter df2 = new DiffFormatter(out)) {
            String diffText;
            df2.setRepository(repo);
			df2.format(diff);
			diffText = out.toString("UTF-8");
			return diffText;
		} catch (Throwable e) {
			return "";
		}
	}

	public synchronized void checkout(String hash) {
        try (Git git = openRepository()) {
            git.reset().setMode(ResetType.HARD).call();
			git.checkout().setName(mainBranchName).call();
			deleteMMBranch(git);
			git.checkout().setCreateBranch(true).setName(BRANCH_MM).setStartPoint(hash).setForce(true).setOrphan(true).call();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private synchronized void deleteMMBranch(Git git) throws GitAPIException {
		List<Ref> refs = git.branchList().call();
		for (Ref r : refs) {
			if (r.getName().endsWith(BRANCH_MM)) {
				git.branchDelete().setBranchNames(BRANCH_MM).setForce(true).call();
				break;
			}
		}
	}

	public synchronized List<RepositoryFile> files() {
		List<RepositoryFile> all = new ArrayList<>();
		for (File f : getAllFilesInPath()) {
			all.add(new RepositoryFile(f));
		}

		return all;
	}

	public synchronized void reset() {
        try (Git git = openRepository()) {
            git.checkout().setName(mainBranchName).setForce(true).call();
			git.branchDelete().setBranchNames(BRANCH_MM).setForce(true).call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<File> getAllFilesInPath() {
		return RDFileUtils.getAllFilesInPath(path);
	}

	@Override
	public long totalCommits() {
		return getChangeSets().size();
	}

	@Override
	@Deprecated
	public String blame(String file, String commitToBeBlamed, Integer line) {
		return blame(file,commitToBeBlamed).get(line).getCommit();
	}

	public List<BlamedLine> blame(String file, String commitToBeBlamed) {
		return blame(file, commitToBeBlamed, true);
	}

	public List<BlamedLine> blame(String file, String commitToBeBlamed, boolean priorCommit) {
        try (Git git = openRepository()) {
			ObjectId gitCommitToBeBlamed;
			if(priorCommit) {
				Iterable<RevCommit> commits = git.log().add(git.getRepository().resolve(commitToBeBlamed)).call();
				gitCommitToBeBlamed = commits.iterator().next().getParent(0).getId();
			} else {
				gitCommitToBeBlamed = git.getRepository().resolve(commitToBeBlamed);
			}

			BlameResult blameResult = git.blame().setFilePath(file).setStartCommit(gitCommitToBeBlamed).setFollowFileRenames(true).call();
			if(blameResult != null) {
				int rows = blameResult.getResultContents().size();
				List<BlamedLine> result = new ArrayList<>();
				for(int i = 0; i < rows; i++) {
					result.add(new BlamedLine(i,
							blameResult.getResultContents().getString(i),
							blameResult.getSourceAuthor(i).getName(),
							blameResult.getSourceCommitter(i).getName(),
							blameResult.getSourceCommit(i).getId().getName()));
				}

				return result;
			} else {
				throw new RuntimeException("BlameResult not found.");
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Integer getMaxNumberFilesInACommit() {
		return maxNumberFilesInACommit;
	}

	@Override
	public String getCommitFromTag(String tag) {

		try (Git git = openRepository()) {
			Repository repo = git.getRepository();

			Iterable<RevCommit> commits = git.log().add(getActualRefObjectId(repo.findRef(tag), repo)).call();

			for(RevCommit commit : commits) {
				return commit.getName().toString();
			}

			throw new RuntimeException("Failed for tag " + tag); // we never arrive here, hopefully

		} catch (Exception e) {
			throw new RuntimeException("Failed for tag " + tag, e);
		}
	}

	private ObjectId getActualRefObjectId(Ref ref, Repository repo) {
		final Ref repoPeeled = repo.peel(ref);
		if(repoPeeled.getPeeledObjectId() != null) {
			return repoPeeled.getPeeledObjectId();
		}
		return ref.getObjectId();
	}

	/**
	 * Return the max number of files in a commit.
	 * Default is hard-coded to "something large".
	 * Override with environment variable "git.maxfiles".
	 *
	 * @return Max number of files in a commit
	 */
	private int checkMaxNumberOfFiles() {
		try {
			return getSystemProperty("git.maxfiles");
		} catch (Exception e) {
			return DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT;
		}
	}

	/**
	 * Return the max size of a diff in bytes.
	 * Default is hard-coded to "something large".
	 * Override with environment variable "git.maxdiff".
	 *
	 * @return Max diff size
	 */
	private int checkMaxSizeOfDiff() {
		try {
			return getSystemProperty("git.maxdiff");
		} catch (Exception e) {
			return MAX_SIZE_OF_A_DIFF;
		}
	}

	/**
	 * Get this system property (environment variable)'s value as an integer.
	 *
	 * @param name	Environment variable to retrieve
	 * @return	{@code name} successfully parsed as an int
	 * @throws NumberFormatException
	 */
	private int getSystemProperty (String name) throws NumberFormatException {
		String val = System.getProperty(name);
		return Integer.parseInt(val);
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setFirstParentOnly(boolean firstParentOnly) {
		this.firstParentOnly = firstParentOnly;
	}

	@Override
	public SCM clone(Path dest) {
		log.info("Cloning to " + dest);
		RDFileUtils.copyDirTree(Paths.get(path), dest);
		return new GitRepository(dest.toString());
	}

	@Override
	public void delete() {
		// allow to be destroyed more than once
		if (RDFileUtils.exists(Paths.get(path))) {
			log.info("Deleting: " + path);
			try {
				FileUtils.deleteDirectory(new File(path.toString()));
			} catch (IOException e) {
				log.info("Delete failed: " + e);
			}
		}
	}

	@Override
	public void setDataToCollect (CollectConfiguration config) {
		this.collectConfig = config;
	}
}
