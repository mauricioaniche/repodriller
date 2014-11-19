package br.com.metricminer2.scm.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import br.com.metricminer2.scm.ChangeSet;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.Committer;
import br.com.metricminer2.scm.ModificationType;
import br.com.metricminer2.scm.SCM;
import br.com.metricminer2.scm.SCMRepository;

public class GitRepository implements SCM {

	private String path;

	public GitRepository(String path) {
		this.path = path;
	}
	
	public static SCMRepository build(String path) {
		return new GitRepository(path).info();
	}
	
	public static SCMRepository[] allIn(String path) {
		List<SCMRepository> repos = new ArrayList<SCMRepository>();
		
		for(String dir : getAllDirsIn(path)) {
			repos.add(build(dir));
		}
		
		return repos.toArray(new SCMRepository[repos.size()]);
	}

	private static String[] getAllDirsIn(String path) {
		File file = new File(path);
		String[] directories = file.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
			});
		return directories;
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

	@Override
	public List<ChangeSet> getChangeSets() {
		try {
			Git git = Git.open(new File(path));

			List<ChangeSet> allCs = new ArrayList<ChangeSet>();

			for (RevCommit r : git.log().all().call()) {
				String hash = r.getName();
				GregorianCalendar date = new GregorianCalendar();
				date.setTime(new Date(r.getCommitTime() * 1000L));

				allCs.add(new ChangeSet(hash, date));
			}

			return allCs;
		} catch (Exception e) {
			throw new RuntimeException("error in getChangeSets for " + path, e);
		}
	}

	@Override
	public Commit detail(String id) {
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

				for (DiffEntry diff : diffsForTheCommit(repo, jgitCommit)) {

					String oldPath = diff.getOldPath();
					String newPath = diff.getNewPath();
					ModificationType change = Enum.valueOf(ModificationType.class, diff.getChangeType().toString());

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

		AnyObjectId hash = repo.resolve(commit.getName());
		AnyObjectId parent = commit.getParentCount() > 0 ? repo.resolve(commit.getParent(0).getName()) : null;

		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repo);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setDetectRenames(true);
		List<DiffEntry> diffs = null;

		if (parent == null) {
			RevWalk rw = new RevWalk(repo);
			diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree()));
			rw.release();
		} else {
			diffs = df.scan(hash, parent);
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

}
