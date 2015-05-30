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

package br.com.metricminer2.scm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
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
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Committer;
import br.com.metricminer2.domain.ModificationType;

public class GitRepository implements SCM {

	private static final int MAX_NUMBER_OF_FILES_IN_A_COMMIT = 30;
	private String path;
	private String currentBranch = "";
	
	private static Logger log = Logger.getLogger(GitRepository.class);

	public GitRepository(String path) {
		this.path = path;
	}
	
	public static SCMRepository singleProject(String path) {
		return new GitRepository(path).info();
	}
	
	public static SCMRepository[] allProjectsIn(String path) {
		List<SCMRepository> repos = new ArrayList<SCMRepository>();
		
		for(String dir : getAllDirsIn(path)) {
			repos.add(singleProject(dir));
		}
		
		return repos.toArray(new SCMRepository[repos.size()]);
	}

	private static List<String> getAllDirsIn(String path) {
		File dir = new File(path);
		String[] files = dir.list();
		
		List<String> projects = new ArrayList<String>();
		for(String file : files) {
			File possibleDir = new File(dir, file);
			if(possibleDir.isDirectory()) {
				projects.add(possibleDir.getAbsolutePath());
			}
		}

		return projects;
	}
	
	public SCMRepository info() {
		RevWalk rw = null;
		try {
			Git git = Git.open(new File(path));
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
			if(rw!=null) rw.release();
		}

	}

	public ChangeSet getHead() {
		try {
			Git git = Git.open(new File(path));
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
		try {
			Git git = Git.open(new File(path));

			List<ChangeSet> allCs = new ArrayList<ChangeSet>();

			for (RevCommit r : git.log().all().call()) {
				String hash = r.getName();
				GregorianCalendar date = convertToDate(r);

				allCs.add(new ChangeSet(hash, date));
			}

			return allCs;
		} catch (Exception e) {
			throw new RuntimeException("error in getChangeSets for " + path, e);
		}
	}

	private GregorianCalendar convertToDate(RevCommit revCommit) {
		GregorianCalendar date = new GregorianCalendar();
		date.setTime(new Date(revCommit.getCommitTime() * 1000L));
		return date;
	}

	@Override
	public Commit getCommit(String id) {
		try {
			Git git = Git.open(new File(path));
			Repository repo = git.getRepository();

			Iterable<RevCommit> commits = git.log().add(repo.resolve(id)).call();
			Commit theCommit = null;

			for (RevCommit jgitCommit : commits) {
				
				Committer committer = new Committer(jgitCommit.getAuthorIdent().getName(), jgitCommit.getAuthorIdent()
						.getEmailAddress());
				String msg = jgitCommit.getFullMessage().trim();
				String hash = jgitCommit.getName().toString();
				long epoch = jgitCommit.getCommitTime();
				String parent = (jgitCommit.getParentCount() > 0) ? jgitCommit.getParent(0).getName().toString() : "";

				GregorianCalendar date = new GregorianCalendar();
				date.setTime(new Date(epoch * 1000L));
				
				theCommit = new Commit(hash, committer, date, msg, parent);

				List<DiffEntry> diffsForTheCommit = diffsForTheCommit(repo, jgitCommit);
				if(diffsForTheCommit.size() > MAX_NUMBER_OF_FILES_IN_A_COMMIT) {
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

					theCommit.addModification(oldPath, newPath, change, diffText, sc);

				}

				break;
			}

			return theCommit;
		} catch (Exception e) {
			throw new RuntimeException("error detailing " + id + " in " + path, e);
		}
	}

	private List<DiffEntry> diffsForTheCommit(Repository repo, RevCommit commit) throws IOException,
			AmbiguousObjectException, IncorrectObjectTypeException {

		AnyObjectId currentCommit = repo.resolve(commit.getName());
		AnyObjectId parentCommit = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null;

		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setBinaryFileThreshold(2 * 1024); // 2 mb max a file
		df.setRepository(repo);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setDetectRenames(true);
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

	private String getSourceCode(Repository repo, DiffEntry diff) throws MissingObjectException, IOException,
			UnsupportedEncodingException {

		try {
			ObjectReader reader = repo.newObjectReader();
			byte[] bytes = reader.open(diff.getNewId().toObjectId()).getBytes();
			return new String(bytes, "utf-8");
		} catch (Throwable e) {
			return "";
		}
	}

	private String getDiffText(Repository repo, DiffEntry diff) throws IOException, UnsupportedEncodingException {
		DiffFormatter df2= null;
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
			if(df2!=null) df2.release();
		}
	}

	public List<File> files(String hash) {
		
		try {
			Git git = Git.open(new File(path));
			currentBranch = git.getRepository().getBranch();
			
			RevCommit commit = git.log().add(git.getRepository().resolve(hash)).call().iterator().next();

			git.branchCreate().setStartPoint(commit).setName("mm").setForce(true).call();
			git.checkout().setName("mm").call();

			List<File> arquivos = getAllFilesInPath();
			
			return arquivos;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public void reset() {
		try {
			Git git = Git.open(new File(path));
			
			if(!currentBranch.isEmpty()) {
				git.checkout().setName(currentBranch).call();
				git.branchDelete().setBranchNames("mm").setForce(true).call();
				currentBranch = "";
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	private List<File> getAllFilesInPath() {
		return getAllFilesInPath(path, new ArrayList<File>());
	}
	private List<File> getAllFilesInPath(String pathToLook, List<File> arquivos) {
		for(File f : new File(pathToLook).listFiles()) {
			if(f.isFile()) arquivos.add(f);
			if(isAProjectSubdirectory(f)) getAllFilesInPath(f.getAbsolutePath(), arquivos);
		}
		return arquivos;
	}

	private boolean isAProjectSubdirectory(File f) {
		return f.isDirectory() && !f.getName().equals(".git");
	}

	@Override
	public long totalCommits() {
		return getChangeSets().size();
	}

}
