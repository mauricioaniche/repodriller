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
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
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
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Developer;
import org.repodriller.domain.ModificationType;
import org.repodriller.util.FileUtils;

public class GitRepository implements SCM {

	private static final int MAX_SIZE_OF_A_DIFF = 100000;
	private static final int DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT = 5000;
	private static final String BRANCH_MM = "mm";

	private String path;
	private String mainBranchName;
	private int maxNumberFilesInACommit;
	private int maxSizeOfDiff;

	private static Logger log = Logger.getLogger(GitRepository.class);
	private boolean firstParentOnly;

	public GitRepository(String path, boolean firstParentOnly) {
		this.path = path;
		this.firstParentOnly = firstParentOnly;
		this.maxNumberFilesInACommit = checkMaxNumberOfFiles();
		this.maxSizeOfDiff = checkMaxSizeOfDiff();
	}

	public GitRepository(String path) {
		this(path, false);
	}

	private int checkMaxNumberOfFiles() {
		String prop = System.getProperty("git.maxfiles");
		if(prop == null) {
			return DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT;
		}
		return Integer.parseInt(prop);
	}

	private int checkMaxSizeOfDiff() {
		String prop = System.getProperty("git.maxdiff");
		if(prop == null) {
			return MAX_SIZE_OF_A_DIFF;
		}
		return Integer.parseInt(prop);
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

		for (String dir : FileUtils.getAllDirsIn(path)) {
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
			throw new RuntimeException("error when info " + path, e);
		}
	}

	protected Git openRepository() throws IOException, GitAPIException {
		Git git = Git.open(new File(path));
		if(this.mainBranchName == null) {
			this.mainBranchName = discoverMainBranchName(git);
		}
		return git;
	}

	private String discoverMainBranchName(Git git) throws IOException {
		return git.getRepository().getBranch();
	}

	public ChangeSet getHead() {
        try (Git git = openRepository()) {
			ObjectId head = git.getRepository().resolve(Constants.HEAD);

			RevWalk revWalk = new RevWalk(git.getRepository());
			RevCommit r = revWalk.parseCommit(head);
			return new ChangeSet(r.getName(), convertToDate(r));

		} catch (Exception e) {
			throw new RuntimeException("error in getHead() for " + path, e);
		}
	}

	@Override
	public List<ChangeSet> getChangeSets() {
        try (Git git = openRepository()) {
            List<ChangeSet> allCs;
			if(!firstParentOnly) allCs = getAllCommits(git);
			else allCs = firstParentsOnly(git);

			return allCs;
		} catch (Exception e) {
            throw new RuntimeException("error in getChangeSets for " + path, e);
        }
	}

	private List<ChangeSet> firstParentsOnly(Git git) {
		try {
			List<ChangeSet> allCs = new ArrayList<>();
			
			RevWalk revWalk = new RevWalk(git.getRepository());
			revWalk.setRevFilter(new FirstParentFilter());
			revWalk.sort(RevSort.TOPO);
			Ref headRef = git.getRepository().getRef(Constants.HEAD);
			RevCommit headCommit = revWalk.parseCommit(headRef.getObjectId());
			revWalk.markStart( headCommit );
			for(RevCommit revCommit : revWalk) {
				allCs.add(extractChangeSet(revCommit));
			}
			
			return allCs;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
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

	@Override
	public Commit getCommit(String id) {
        try (Git git = openRepository()) {
			Repository repo = git.getRepository();

			Iterable<RevCommit> commits = git.log().add(repo.resolve(id)).call();
			Commit theCommit = null;

			for (RevCommit jgitCommit : commits) {

				Developer author = new Developer(jgitCommit.getAuthorIdent().getName(), jgitCommit.getAuthorIdent().getEmailAddress());
				Developer committer = new Developer(jgitCommit.getCommitterIdent().getName(), jgitCommit.getCommitterIdent().getEmailAddress());

				TimeZone authorTimeZone = jgitCommit.getAuthorIdent().getTimeZone();
				TimeZone committerTimeZone = jgitCommit.getCommitterIdent().getTimeZone();
				
				String msg = jgitCommit.getFullMessage().trim();
				String hash = jgitCommit.getName().toString();
				String parent = (jgitCommit.getParentCount() > 0) ? jgitCommit.getParent(0).getName().toString() : "";

				GregorianCalendar authorDate = new GregorianCalendar();
				authorDate.setTime(jgitCommit.getAuthorIdent().getWhen());
				authorDate.setTimeZone(jgitCommit.getAuthorIdent().getTimeZone());

				GregorianCalendar committerDate = new GregorianCalendar();
				committerDate.setTime(jgitCommit.getCommitterIdent().getWhen());
				committerDate.setTimeZone(jgitCommit.getCommitterIdent().getTimeZone());
				
				boolean merge = false;
				if(jgitCommit.getParentCount() > 1) merge = true;
				Set<String> branches = getBranches(git, hash);
				boolean isCommitInMainBranch = branches.contains(this.mainBranchName);
				theCommit = new Commit(hash, author, committer, authorDate, authorTimeZone, committerDate, committerTimeZone, msg, parent, merge, branches, isCommitInMainBranch);

				List<DiffEntry> diffsForTheCommit = diffsForTheCommit(repo, jgitCommit);
				if (diffsForTheCommit.size() > this.getMaxNumberFilesInACommit()) {
					log.error("commit " + id + " has more than files than the limit");
					throw new RuntimeException("commit " + id + " too big, sorry");
				}

				for (DiffEntry diff : diffsForTheCommit) {

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
					
					theCommit.addModification(oldPath, newPath, change, diffText, sc);

				}

				break;
			}

			return theCommit;
		} catch (Exception e) {
			throw new RuntimeException("error detailing " + id + " in " + path, e);
		}
	}


	private Set<String> getBranches(Git git, String hash) throws GitAPIException {
		List<Ref> gitBranches = git.branchList().setContains(hash).call();
		Set<String> mappedBranches = gitBranches.stream()
				.map(
					(ref) -> ref.getName().substring(ref.getName().lastIndexOf("/")+1))
				.collect(Collectors.toSet());
		return mappedBranches;
	}

	private List<DiffEntry> diffsForTheCommit(Repository repo, RevCommit commit) throws IOException {

		AnyObjectId currentCommit = repo.resolve(commit.getName());
		AnyObjectId parentCommit = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null;
    
		try (DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE)) {

            df.setBinaryFileThreshold(2 * 1024); // 2 mb max a file
            df.setRepository(repo);
            df.setDiffComparator(RawTextComparator.DEFAULT);
            df.setDetectRenames(true);
            setContext(df);

            List<DiffEntry> diffs = null;

            if (parentCommit == null) {
                try(RevWalk rw = new RevWalk(repo)) {
                    diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree()));
                }
            } else {
                diffs = df.scan(parentCommit, currentCommit);
            }
            return diffs;
        }
	}

	private void setContext(DiffFormatter df) {
		String context = System.getProperty("git.diffcontext");
		if(context==null) return;
		df.setContext(Integer.parseInt(System.getProperty("git.diffcontext")));
	}

	private String getSourceCode(Repository repo, DiffEntry diff) throws IOException {
		try {
			ObjectReader reader = repo.newObjectReader();
			byte[] bytes = reader.open(diff.getNewId().toObjectId()).getBytes();
			return new String(bytes, "utf-8");
		} catch (Throwable e) {
			return "";
		}
	}

	private String getDiffText(Repository repo, DiffEntry diff) throws IOException {
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

	public List<RepositoryFile> files() {
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
		return FileUtils.getAllFilesInPath(path);
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

			Iterable<RevCommit> commits = git.log().add(repo.resolve(tag)).call();
			
			for(RevCommit commit : commits) {
				return commit.getName().toString();
			}
			
			throw new RuntimeException("Failed for tag " + tag); // we never arrive here, hopefully

		} catch (Exception e) {
			throw new RuntimeException("Failed for tag " + tag, e);
		}
	}
}
