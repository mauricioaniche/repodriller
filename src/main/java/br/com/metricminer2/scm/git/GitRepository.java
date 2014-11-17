package br.com.metricminer2.scm.git;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import br.com.metricminer2.scm.ChangeSet;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.Committer;
import br.com.metricminer2.scm.ModificationType;
import br.com.metricminer2.scm.SCM;

public class GitRepository implements SCM {

	@Override
	public List<ChangeSet> getChangeSets(String path) {
		try {
			Git git = Git.open(new File(path));

			List<ChangeSet> allCs = new ArrayList<ChangeSet>();
			
			for(RevCommit r : git.log().all().call()) {
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
	public Commit detail(String id, String path) {
		try {
			Git git = Git.open(new File(path));
			Repository repo = git.getRepository();
			
			Iterable<RevCommit> commits = git.log().add(repo.resolve(id)).call();
			Commit theCommit = null;
					
			for(RevCommit jgitCommit : commits) {
				
				Committer committer = new Committer(jgitCommit.getAuthorIdent().getName(), jgitCommit.getAuthorIdent().getEmailAddress());
				String msg = jgitCommit.getFullMessage();
				String hash = jgitCommit.getName().toString();
				String parent = (jgitCommit.getParentCount()>0) ? jgitCommit.getParent(0).getName().toString() : "";
				
				theCommit = new Commit(hash, committer, msg, parent);

				for(DiffEntry diff : diffsForTheCommit(repo, jgitCommit)) {
					
					String oldPath = diff.getOldPath();
					String newPath = diff.getNewPath();
					ModificationType change = Enum.valueOf(ModificationType.class, diff.getChangeType().toString());
					
					String diffText = "";
					String sc = "";
					if(diff.getChangeType() != ChangeType.DELETE) {
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
		
		String hash = commit.getName().toString();
		String parent = commit.getParent(0).getName().toString();
		
		DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		df.setRepository(repo);
		df.setDiffComparator(RawTextComparator.DEFAULT);
		df.setDetectRenames(true);
		List<DiffEntry> diffs = df.scan(repo.resolve(hash), repo.resolve(parent));
		return diffs;
	}

	private String getSourceCode(Repository repo, DiffEntry diff) throws MissingObjectException, IOException,
			UnsupportedEncodingException {
		
		ObjectReader reader = repo.newObjectReader();
		byte[] bytes = reader.open(diff.getNewId().toObjectId()).getBytes();
		return new String(bytes, "utf-8");
	}

	private String getDiffText(Repository repo, DiffEntry diff) throws IOException, UnsupportedEncodingException {
		String diffText;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		DiffFormatter df2 = new DiffFormatter(out);
		df2.setRepository(repo);
		df2.format(diff);
		diffText = out.toString("UTF-8");
		return diffText;
	}

}
