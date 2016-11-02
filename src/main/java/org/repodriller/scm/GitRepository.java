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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
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
	private static final int DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT = 200;
	private static final String BRANCH_MM = "mm";

	private String path;
	private String mainBranchName;
	private int maxNumberFilesInACommit;
	private int maxSizeOfDiff;

	private static Logger log = Logger.getLogger(GitRepository.class);

	public GitRepository(String path) {
		this.path = path;
		this.maxNumberFilesInACommit = checkMaxNumberOfFiles();
		this.maxSizeOfDiff = checkMaxSizeOfDiff();
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
		return singleProject(path, DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT);
	}

	public static SCMRepository singleProject(String path, Integer maxNumberOfFilesInACommit) {
		return new GitRepository(path).info();
	}

	public static SCMRepository[] allProjectsIn(String path) {
		return allProjectsIn(path, DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT);
	}

	public static SCMRepository[] allProjectsIn(String path, Integer maxNumberOfFilesInACommit) {
		List<SCMRepository> repos = new ArrayList<SCMRepository>();

		for (String dir : FileUtils.getAllDirsIn(path)) {
			repos.add(singleProject(dir, maxNumberOfFilesInACommit));
		}

		return repos.toArray(new SCMRepository[repos.size()]);
	}

	public SCMRepository info() {
		RevWalk rw = null;
		Git git = null;
		try {
			git = openRepository();
			AnyObjectId headId = git.getRepository().resolve(Constants.HEAD);

			rw = new RevWalk(git.getRepository());
			RevCommit root = rw.parseCommit(headId);
			rw.sort(RevSort.REVERSE);
			rw.markStart(root);
			RevCommit lastCommit = rw.next();

			String origin = git.getRepository().getConfig().getString("remote", "origin", "url");

			return new SCMRepository(this, origin, path, headId.getName(), lastCommit.getName());
		} catch (Exception e) {
			throw new RuntimeException("error when info " + path, e);
		} finally {
			if (rw != null)
				rw.release();
			if (git != null)
				git.close();
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
		Git git = null;
		try {
			git = openRepository();
			ObjectId head = git.getRepository().resolve(Constants.HEAD);

			RevWalk revWalk = new RevWalk(git.getRepository());
			RevCommit r = revWalk.parseCommit(head);
			return new ChangeSet(r.getName(), convertToDate(r));

		} catch (Exception e) {
			throw new RuntimeException("error in getHead() for " + path, e);
		} finally {
			if (git != null)
				git.close();
		}

	}

	@Override
	public List<ChangeSet> getChangeSets() {
		Git git = null;
		try {
			git = openRepository();

			List<ChangeSet> allCs = new ArrayList<ChangeSet>();

			for (RevCommit r : git.log().all().call()) {
				String hash = r.getName();
				GregorianCalendar date = convertToDate(r);

				allCs.add(new ChangeSet(hash, date));
			}

			return allCs;
		} catch (Exception e) {
			throw new RuntimeException("error in getChangeSets for " + path, e);
		} finally {
			if (git != null)
				git.close();
		}
	}

	private GregorianCalendar convertToDate(RevCommit revCommit) {
		GregorianCalendar date = new GregorianCalendar();
		date.setTimeZone(revCommit.getAuthorIdent().getTimeZone());
		date.setTime(revCommit.getAuthorIdent().getWhen());
		
		return date;
	}

	@Override
	public Commit getCommit(String id) {
		Git git = null;
		try {
			git = openRepository();
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
		} finally {
			if (git != null)
				git.close();
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

	private List<DiffEntry> diffsForTheCommit(Repository repo, RevCommit commit) throws IOException, AmbiguousObjectException,
			IncorrectObjectTypeException {

		AnyObjectId currentCommit = repo.resolve(commit.getName());
		AnyObjectId parentCommit = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null;

		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setBinaryFileThreshold(2 * 1024); // 2 mb max a file
		df.setRepository(repo);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setDetectRenames(true);
		setContext(df);
		
		List<DiffEntry> diffs = null;

		if (parentCommit == null) {
			RevWalk rw = new RevWalk(repo);
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree()));
			rw.release();
		} else {
			diffs = df.scan(parentCommit, currentCommit);
		}

		df.release();

		return diffs;
	}

	private void setContext(DiffFormatter df) {
		String context = System.getProperty("git.diffcontext");
		if(context==null) return;
		df.setContext(Integer.parseInt(System.getProperty("git.diffcontext")));
	}

	private String getSourceCode(Repository repo, DiffEntry diff) throws MissingObjectException, IOException, UnsupportedEncodingException {

		try {
			ObjectReader reader = repo.newObjectReader();
			byte[] bytes = reader.open(diff.getNewId().toObjectId()).getBytes();
			return new String(bytes, "utf-8");
		} catch (Throwable e) {
			return "";
		}
	}

	private String getDiffText(Repository repo, DiffEntry diff) throws IOException, UnsupportedEncodingException {
		DiffFormatter df2 = null;
		try {
			String diffText;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			df2 = new DiffFormatter(out);
			df2.setRepository(repo);
			df2.format(diff);
			diffText = out.toString("UTF-8");
			return diffText;
		} catch (Throwable e) {
			return "";
		} finally {
			if (df2 != null)
				df2.release();
		}
	}

	public synchronized void checkout(String hash) {
		Git git = null;
		try {
			git = openRepository();
			git.reset().setMode(ResetType.HARD).call();
			git.checkout().setName(mainBranchName).call();
			deleteMMBranch(git);
			git.checkout().setCreateBranch(true).setName(BRANCH_MM).setStartPoint(hash).setForce(true).setOrphan(true).call();

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (git != null)
				git.close();
		}
	}

	private synchronized void deleteMMBranch(Git git) throws GitAPIException, NotMergedException, CannotDeleteCurrentBranchException {
		List<Ref> refs = git.branchList().call();
		for (Ref r : refs) {
			if (r.getName().endsWith(BRANCH_MM)) {
				git.branchDelete().setBranchNames(BRANCH_MM).setForce(true).call();
				break;
			}
		}
	}

	public List<RepositoryFile> files() {
		List<RepositoryFile> all = new ArrayList<RepositoryFile>();
		for (File f : getAllFilesInPath()) {
			all.add(new RepositoryFile(f));
		}

		return all;
	}

	public synchronized void reset() {
		Git git = null;
		try {
			git = openRepository();

			git.checkout().setName(mainBranchName).setForce(true).call();
			git.branchDelete().setBranchNames(BRANCH_MM).setForce(true).call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (git != null)
				git.close();
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
		Git git = null;
		try {
			git = openRepository();

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
		} finally {
			if (git != null)
				git.close();
		}
	}
	
	public Integer getMaxNumberFilesInACommit() {
		return maxNumberFilesInACommit;
	}

}
