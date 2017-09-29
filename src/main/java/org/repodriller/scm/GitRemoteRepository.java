package org.repodriller.scm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.repodriller.RepoDrillerException;
import org.repodriller.util.RDFileUtils;

/**
 * A GitRepository that knows how to clone a remote repo and clean up after itself.
 * Instantiating a GitRemoteRepository will clone the specified repo, which
 *  - is expensive
 *  - may throw an exception
 *
 * @author Jamie Davis
 */
/* TODO Requiring cloning at instantiation-time is not "lightweight".
 *      It means the user won't get any results until after cloning every repo being analyzed.
 *      I suggest we only actually clone when the clone() method of an SCM is invoked.
 *      We should override GitRepository's cheap "copy" implementation of clone() and actually clone then. */
 /* TODO Remove the AutoCloseable interface; the new clone/delete methods in SCM make things clearer. */
public class GitRemoteRepository extends GitRepository implements AutoCloseable {

	/* Constants. */
	public static final String URL_SUFFIX = ".git";

	/* Internal. */
	private boolean hasLocalState = false;

	/* User-defined. */
	private String uri;
	private String path; /* TODO GitRepository also has a path member. Make it protected and inherit, or use getter/setter as needed? */

	private static Logger log = Logger.getLogger(GitRemoteRepository.class);

	/**
	 * @param uri	Where do we clone the repo from?
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public GitRemoteRepository(String uri) {
		this(uri, null, false);
	}

	/**
	 * @param uri	Where do we clone the repo from? Anything that works as an argument to "git clone", e.g. local dir or GitHub address.
	 * @param destination	If provided, clone here. Should not exist already.
	 *                   	If null, clones to a unique temp dir.
	 * @param bare	Bare clone (metadata only) or full?
	 */
	public GitRemoteRepository(String uri, String destination, boolean bare) {
		super();

		try {
			/* Set members. */
			this.uri = uri;

			/* Figure out clone path. */
			path = destination;
			if (path == null) {
				/* Pick a temp dir name. */
				String tempDirPath = RDFileUtils.getTempPath(null); // all ancestors exist
				String repoName = repoNameFromURI(uri);
				path = tempDirPath + "-" + repoName;
			}

			/* path must not exist already. */
			if (new File(path).exists()) {
				throw new RepoDrillerException("Error, path " + path + " already exists");
			}

			log.info("url " + uri + " destination " + destination + " bare " + bare + " (path " + path + ")");

			/* Fill in GitRepository details. */
			this.setPath(path);
			this.setFirstParentOnly(true); /* TODO. */

			/* Clone the remote repo. */
			cloneGitRepository(uri, path, bare);
			hasLocalState = true;
		} catch (IOException|GitAPIException|RepoDrillerException e) {
			log.error("Unsuccessful git remote repository initialization", e);
			throw new RepoDrillerException(e);
		}
	}

	/**
	 * Clone a git repository.
	 *
	 * @param uri	Where from?
	 * @param destination	Where to?
	 * @param bare	Bare (metadata-only) or full?
	 * @throws GitAPIException
	 */
	private void cloneGitRepository(String uri, String destination, boolean bare) throws GitAPIException {
		File directory = new File(destination);

		if (directory.exists())
			throw new RepoDrillerException("Error, destination " + destination + " already exists");

		log.info("Cloning Remote Repository " + uri + " into " + this.path);
		Git.cloneRepository()
				.setURI(uri)
				.setBare(bare)
				.setDirectory(directory)
				.setCloneAllBranches(true)
				.setNoCheckout(false)
				.call();
	}

	/**
	 * Extract a git repo name from its URL.
	 *
	 * @param uri
	 * @return
	 */
	private static String repoNameFromURI(String uri) {
		/* Examples:
		 *   git@github.com:substack/node-mkdirp.git
		 *   https://bitbucket.org/fenics-project/notebooks.git
		 *   /tmp/existing-git-dir
		 */
		int lastSlashIx = uri.lastIndexOf("/");

		int lastSuffIx = uri.lastIndexOf(URL_SUFFIX);
		if (lastSuffIx < 0)
			lastSuffIx = uri.length();

		if (lastSlashIx < 0 || lastSuffIx <= lastSlashIx)
			throw new RepoDrillerException("Error, ill-formed url: " + uri);

		return uri.substring(lastSlashIx + 1, lastSuffIx);
	}

	/**
	 * Clean up this object.
	 * See {@link GitRemoteRepository#close}.
	 *
	 * @throws IOException
	 */
	@Deprecated
	public void deleteTempGitPath() throws IOException {
		close();
	}

	/* Various factory methods. */

	public static SCMRepository singleProject(String url) {
		return singleProject(url, null, false);
	}

	@SuppressWarnings("resource")
	public static SCMRepository singleProject(String url, String rootpath, boolean bare) {
		return new GitRemoteRepository(url, rootpath, bare).info();
	}

	public static SCMRepository[] allProjectsIn(List<String> urls) throws GitAPIException, IOException {
		return allProjectsIn(urls, null, false);
	}

	protected static SCMRepository[] allProjectsIn(List<String> urls, String rootpath, boolean bare) {
		List<SCMRepository> repos = new ArrayList<SCMRepository>();
		for (String url : urls) {
			repos.add(singleProject(url, rootpath, bare));
		}

		return repos.toArray(new SCMRepository[repos.size()]);
	}

	public static SingleGitRemoteRepositoryBuilder hostedOn(String gitUrl) {
		return new SingleGitRemoteRepositoryBuilder(gitUrl);
	}

	public static MultipleGitRemoteRepositoryBuilder hostedOn(List<String> gitUrls) {
		return new MultipleGitRemoteRepositoryBuilder(gitUrls);
	}

	/* Interface: AutoCloseable. */

	@Override
	public void close() throws IOException {
		if (hasLocalState)
			FileUtils.deleteDirectory(new File(path));
		hasLocalState = false;
	}
	
	/* TODO Override clone and delete. */
}
