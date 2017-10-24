package org.repodriller.scm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.jgit.lib.PersonIdent;
import org.repodriller.RepoDrillerException;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.domain.CommitContributor;
import org.repodriller.domain.Modification;
import org.repodriller.domain.ModificationType;
import org.repodriller.util.RDFileUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

/**
 * @author Juliano Silva
 *
 */
/* TODO Name: Sounds like it inherits SCMRepository, but it actually implements SCM. */
public class SubversionRepository implements SCM {

	private static final int MAX_SIZE_OF_A_DIFF = 100000;
	private static final int DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT = 50;

	private static Logger log = Logger.getLogger(SubversionRepository.class);
	private String path;
	private String username;
	private String password;
	private String workingCopyPath;
	private Integer maxNumberFilesInACommit;

	public SubversionRepository(String path, String username, String password) {
		this(path, username, password, DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT);
	}

	public SubversionRepository(String path, String username, String password, Integer maxNumberOfFilesInACommit) {
		this.path = path;
		this.username = username;
		this.password = password;
		maxNumberOfFilesInACommit = checkMaxNumber(maxNumberOfFilesInACommit);
		this.maxNumberFilesInACommit = maxNumberOfFilesInACommit;

		workingCopyPath = createWorkingCopy();
	}

	private Integer checkMaxNumber(Integer maxNumberOfFilesInACommit) {
		if(maxNumberOfFilesInACommit == null) {
			maxNumberOfFilesInACommit = DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT;
		}
		if(maxNumberOfFilesInACommit <= 0){
			throw new IllegalArgumentException("Max number of files in a commit should be 0 or greater."
					+ "Default value is " + DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT);
		}
		return maxNumberOfFilesInACommit;
	}

	public SubversionRepository(String repositoryPath) {
		this(repositoryPath, null, null, DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT);
	}

	public SubversionRepository(String repositoryPath, Integer maxNumberOfFilesInACommit) {
		this(repositoryPath, null, null, maxNumberOfFilesInACommit);
	}

	public static SCMRepository singleProject(String path) {
		return singleProject(path, DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT);
	}

	public static SCMRepository singleProject(String path, Integer maxNumberOfFilesInACommit) {
		return new SubversionRepository(path, maxNumberOfFilesInACommit).info();
	}

	public static SCMRepository[] allProjectsIn(String path) {
		return allProjectsIn(path, DEFAULT_MAX_NUMBER_OF_FILES_IN_A_COMMIT);
	}

	public static SCMRepository[] allProjectsIn(String path, Integer maxNumberOfFilesInACommit) {
		List<SCMRepository> repos = new ArrayList<SCMRepository>();

		for (String dir : RDFileUtils.getAllDirsIn(path)) {
			repos.add(singleProject(dir, maxNumberOfFilesInACommit));
		}

		return repos.toArray(new SCMRepository[repos.size()]);
	}

	public SCMRepository info() {
		SVNRepository repository = null;
		try {
			SVNURL url = SVNURL.parseURIEncoded(path);
			repository = SVNRepositoryFactory.create(url);

			authenticateIfNecessary(repository);

			SVNDirEntry firstRevision = repository.info("/", 0);
			SVNDirEntry lastRevision = repository.info("/", SVNRevision.HEAD.getNumber());

			return new SCMRepository(this, lastRevision.getURL().getPath(), path, String.valueOf(lastRevision.getRevision()), String.valueOf(firstRevision
					.getRevision()));

		} catch (SVNException e) {
			throw new RuntimeException("error in getHead() for " + path, e);
		} finally {
			if (repository != null)
				repository.closeSession();
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<ChangeSet> getChangeSets() {
		SVNRepository repository = null;

		try {
			SVNURL url = SVNURL.parseURIEncoded(path);
			repository = SVNRepositoryFactory.create(url);

			authenticateIfNecessary(repository);

			List<ChangeSet> allCs = new ArrayList<ChangeSet>();

			long startRevision = 0;
			long endRevision = -1; // HEAD (the latest) revision
			Collection log = repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);
			for (Iterator iterator = log.iterator(); iterator.hasNext();) {
				SVNLogEntry entry = (SVNLogEntry) iterator.next();
				allCs.add(extractChangeSet(entry));
			}

			return allCs;

		} catch (SVNException e) {
			throw new RuntimeException("error in getHead() for " + path, e);
		} finally {
			if (repository != null)
				repository.closeSession();
		}
	}

	private ChangeSet extractChangeSet(SVNLogEntry entry) {
		String id = String.valueOf(entry.getRevision());
		String msg = entry.getMessage();
		GregorianCalendar commitTime = new GregorianCalendar();
		commitTime.setTime(entry.getDate());
		CommitContributor author = new CommitContributor(entry.getAuthor(), null, commitTime, commitTime.getTimeZone());

		return new ChangeSet(id, msg, author, null, new TreeSet<String>(), new TreeSet<String>(), false);
	}

	private ChangeSet extractChangeSet(SVNDirEntry entry) {
		String id = String.valueOf(entry.getRevision());
		String msg = entry.getCommitMessage();
		GregorianCalendar commitTime = new GregorianCalendar();
		commitTime.setTime(entry.getDate());
		CommitContributor author = new CommitContributor(entry.getAuthor(), null, commitTime, commitTime.getTimeZone());

		return new ChangeSet(id, msg, author, null, new TreeSet<String>(), new TreeSet<String>(), false);
	}

	private GregorianCalendar extractCalendar(PersonIdent pi) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeZone(pi.getTimeZone());
		cal.setTime(pi.getWhen());
		return cal;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Commit getCommit(String id) {
		SVNRepository repository = null;

		try {
			SVNURL url = SVNURL.parseURIEncoded(path);
			repository = SVNRepositoryFactory.create(url);

			authenticateIfNecessary(repository);

			long revision = Long.parseLong(id);
			long startRevision = revision;
			long endRevision = revision;

			@SuppressWarnings("unchecked")
			Iterable<SVNLogEntry> repositoryLog = repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);
			Iterator<SVNLogEntry> itr = repositoryLog.iterator();
			if (!itr.hasNext())
				return null;
			SVNLogEntry logEntry = itr.next();

			Commit commit = createCommit(logEntry);
			addModificationsToCommit(repository,  url,  revision,  logEntry,  commit);

			return commit;
		} catch (Exception e) {
			throw new RuntimeException("error in getCommit() for " + path, e);
		} finally {
			if (repository != null)
				repository.closeSession();
		}
	}

	public Commit getCommit(ChangeSet cs) {
		SVNRepository repository = null;

		try {
			SVNURL url = SVNURL.parseURIEncoded(path);
			repository = SVNRepositoryFactory.create(url);

			authenticateIfNecessary(repository);

			long revision = Long.parseLong(cs.getId());
			long startRevision = revision;
			long endRevision = revision;

			@SuppressWarnings("unchecked")
			Iterable<SVNLogEntry> repositoryLog = repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);
			Iterator<SVNLogEntry> itr = repositoryLog.iterator();
			if (!itr.hasNext())
				return null;
			SVNLogEntry logEntry = itr.next();

			Commit commit = new Commit(cs);
			addModificationsToCommit(repository,  url,  revision,  logEntry,  commit);

			return commit;
		} catch (Exception e) {
			throw new RuntimeException("error in getCommit() for " + path, e);
		} finally {
			if (repository != null)
				repository.closeSession();
		}
	}

	/**
	 * Add each of the Modifications associated with {@code revCommit} to {@code commit}.
	 *
	 * @param repo
	 * @param revCommit
	 * @param commit
	 * @throws SVNException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void addModificationsToCommit(SVNRepository repository, SVNURL url, long revision, SVNLogEntry logEntry, Commit commit) throws UnsupportedEncodingException, SVNException {
		List<Modification> modifications = getModifications(repository, url, revision, logEntry);

		if (modifications.size() > this.maxNumberFilesInACommit) {
			log.error("commit " + commit.getId() + " has more than files than the limit");
			throw new RuntimeException("commit " + commit.getId() + " too big, sorry");
		}
		commit.addModifications(modifications);
	}

	private Commit createCommit(SVNLogEntry logEntry) {
		return new Commit(extractChangeSet(logEntry));
	}

	private List<Modification> getModifications(SVNRepository repository, SVNURL url, long revision, SVNLogEntry logEntry) throws SVNException,
			UnsupportedEncodingException {

		List<Modification> modifications = new ArrayList<Modification>();
		for (Entry<String, SVNLogEntryPath> entry : logEntry.getChangedPaths().entrySet()) {
			SVNLogEntryPath e = entry.getValue();

			String diffText = getDiffText(repository, url, e, revision);

			String sc = getSourceCode(repository, revision, e);

			Modification modification = new Modification(e.getCopyPath(), e.getPath(), getModificationType(e), diffText, sc);
			modifications.add(modification);
		}

		return modifications;
	}

	private String getSourceCode(SVNRepository repository, long endRevision, SVNLogEntryPath e) throws SVNException, UnsupportedEncodingException {
		if (e.getType() == 'D')
			return "";

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		repository.getFile(e.getPath(), endRevision, null, out);

		String sc = out.toString("UTF-8");
		return sc;
	}

	private String getDiffText(SVNRepository repository, SVNURL url, SVNLogEntryPath entry, long revision) {
		try {
			SVNClientManager clientManager = SVNClientManager.newInstance(null, repository.getAuthenticationManager());
			SVNDiffClient diffClient = clientManager.getDiffClient();

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			SVNRevision startRevision = SVNRevision.create(revision - 1);
			SVNRevision endRevision = SVNRevision.create(revision);

			diffClient.doDiff(url, startRevision, startRevision, endRevision, SVNDepth.FILES, true, out);

			String diffText = out.toString("UTF-8");
			if (diffText.length() > MAX_SIZE_OF_A_DIFF) {
				log.error("diff for " + entry.getPath() + " too big");
				diffText = "-- TOO BIG --";
			}
			return diffText;

		} catch (Exception e) {
			return "";
		}
	}

	@Override
	public List<Modification> getDiffBetweenCommits(String priorCommit, String laterCommit) {
		// TODO Not yet implemented for SVN.
		throw new RepoDrillerException("This feature has not yet been implemented for Subversion repos.");
	}

	private ModificationType getModificationType(SVNLogEntryPath e) {
		if (e.getType() == 'A') {
			return ModificationType.ADD;
		} else if (e.getType() == 'D') {
			return ModificationType.DELETE;
		} else if (e.getType() == 'M') {
			return ModificationType.MODIFY;
		} else if (e.getType() == 'R') {
			return ModificationType.COPY;
		}
		return null;
	}

	@Override
	public ChangeSet getHead() {
		SVNRepository repository = null;

		try {
			SVNURL url = SVNURL.parseURIEncoded(path);
			repository = SVNRepositoryFactory.create(url);

			authenticateIfNecessary(repository);

			SVNDirEntry entry = repository.info("/", -1);
			return extractChangeSet(entry);
		} catch (SVNException e) {
			throw new RuntimeException("error in getHead() for " + path, e);
		} finally {
			if (repository != null)
				repository.closeSession();
		}
	}

	private GregorianCalendar convertToCalendar(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar;
	}

	@Override
	public List<RepositoryFile> files() {
		List<RepositoryFile> all = new ArrayList<RepositoryFile>();
		for (File f : getAllFilesInPath()) {
			if (isNotAnImportantFile(f))
				continue;
			all.add(new RepositoryFile(f));
		}

		return all;
	}

	private List<File> getAllFilesInPath() {
		return RDFileUtils.getAllFilesInPath(workingCopyPath);
	}

	private boolean isNotAnImportantFile(File f) {
		return f.getName().equals(".DS_Store");
	}

	@Override
	public long totalCommits() {
		return getChangeSets().size();
	}

	@Override
	public void reset() {
		SVNRepository repository = null;
		try {
			SVNRevision revision = SVNRevision.HEAD;

			SVNURL url = SVNURL.parseURIEncoded(path);
			repository = SVNRepositoryFactory.create(url);

			authenticateIfNecessary(repository);

			SVNClientManager ourClientManager = SVNClientManager.newInstance(null, repository.getAuthenticationManager());
			SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
			updateClient.setIgnoreExternals(false);
			updateClient.doCheckout(url, new File(workingCopyPath), revision, revision, SVNDepth.INFINITY, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (repository != null)
				repository.closeSession();
		}
	}

	@Override
	public void checkout(String id) {
		SVNRepository repository = null;
		try {
			clearWorkingCopy();

			SVNRevision revision = SVNRevision.create(Integer.parseInt(id));

			SVNURL url = SVNURL.parseURIEncoded(path);
			repository = SVNRepositoryFactory.create(url);

			authenticateIfNecessary(repository);

			SVNClientManager ourClientManager = SVNClientManager.newInstance(null, repository.getAuthenticationManager());
			SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
			updateClient.setIgnoreExternals(false);
			updateClient.doCheckout(url, new File(workingCopyPath), revision, revision, SVNDepth.INFINITY, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (repository != null)
				repository.closeSession();
		}
	}

	private void clearWorkingCopy() {
		try {
			org.apache.commons.io.FileUtils.cleanDirectory(new File(workingCopyPath));
		} catch (IOException e) {
			throw new RuntimeException("Unable to clean working copy path", e);
		}
	}

	@Override
	@Deprecated
	public String blame(String file, String currentCommit, Integer line) {
		try {
			SVNURL url = SVNURL.parseURIEncoded(path + File.separator + file);

			ISVNAuthenticationManager authManager = getAuthenticationManager();

			SVNLogClient logClient = SVNClientManager.newInstance(null, authManager).getLogClient();
			boolean ignoreMimeType = false;
			boolean includeMergedRevisions = false;

			logClient.doAnnotate(url, SVNRevision.UNDEFINED, SVNRevision.create(Integer.parseInt(currentCommit)), SVNRevision.HEAD, ignoreMimeType,
					includeMergedRevisions, null, null);

			return String.valueOf(SVNRevision.create(Integer.parseInt(currentCommit)).getNumber());

		} catch (SVNException e) {
			throw new RuntimeException(e);
		}
	}

	private void authenticateIfNecessary(SVNRepository repository) {
		ISVNAuthenticationManager authManager = getAuthenticationManager();
		if (authManager != null)
			repository.setAuthenticationManager(authManager);
	}

	private BasicAuthenticationManager getAuthenticationManager() {
		if (username != null && password != null) {
			return BasicAuthenticationManager.newInstance(username, password.toCharArray());
		}
		return null;
	}

	private String createWorkingCopy() {
		String tmpDirPath = System.getProperty("java.io.tmpdir");
		File tmpDir = new File(tmpDirPath + File.separator + "metricminer");
		if (!tmpDir.exists()) {
			boolean created = tmpDir.mkdirs();
			if (!created) {
				throw new RuntimeException("Unable to create temporary folder for working copy in " + tmpDir);
			}
		}

		return tmpDir.getPath();
	}

	public String getPath() {
		return path;
	}

	@Override
	public List<BlamedLine> blame(String file, String currentCommit, boolean priorCommit) {
		// pull request me!
		throw new RuntimeException("implement me!");
	}

	public Integer getMaxNumberFilesInACommit() {
		return maxNumberFilesInACommit;
	}

	@Override
	public String getCommitFromTag(String tag) {
		// pull request me!
		throw new RuntimeException("implement me!");
	}

}
