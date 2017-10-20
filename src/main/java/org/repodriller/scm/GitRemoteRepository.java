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
public class GitRemoteRepository extends GitRepository implements AutoCloseable {

	/* Constants. */
	public static final String URL_SUFFIX = ".git";

	/* Internal. */
	private boolean hasLocalState = false;

	/* User-defined. */
	private String url;
	private String path; /* TODO GitRepository also has a path member. Make it protected and inherit, or use getter/setter as needed? */

	private static Logger log = Logger.getLogger(GitRemoteRepository.class);

	/**
	 * @param url	Where do we clone the repo from?
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public GitRemoteRepository(String url) {
		this(url, null, false);
	}

	/**
	 * @param url	Where do we clone the repo from?
	 * @param destination	If provided, clone here. Should not exist already.
	 *                   	If null, clones to a unique temp dir.
	 * @param bare	Bare clone (metadata only) or full?
	 */
	public GitRemoteRepository(String url, String destination, boolean bare) {
		super();

		try {
			/* Set members. */
			this.url = url;

			/* Figure out clone path. */
			path = destination;
			if (path == null) {
				/* Pick a temp dir name. */
				String tempDirPath = RDFileUtils.getTempPath(null); // all ancestors exist
				String repoName = repoNameFromURL(url);
				path = tempDirPath + "-" + repoName;
			}

			/* path must not exist already. */
			if (new File(path).exists()) {
				throw new RepoDrillerException("Error, path " + path + " already exists");
			}

			log.info("url " + url + " destination " + destination + " bare " + bare + " (path " + path + ")");

			/* Fill in GitRepository details. */
			this.setPath(path);
			this.setFirstParentOnly(true); /* TODO. */

			/* Clone the remote repo. */
			cloneGitRepository(url, path, bare);
			hasLocalState = true;
		} catch (IOException|GitAPIException|RepoDrillerException e) {
			log.error("Unsuccessful git remote repository initialization", e);
			throw new RepoDrillerException(e);
		}
	}

	/**
	 * Clone a git repository.
	 *
	 * @param url	Where from?
	 * @param destination	Where to?
	 * @param bare	Bare (metadata-only) or full?
	 * @throws GitAPIException
	 */
	private void cloneGitRepository(String url, String destination, boolean bare) throws GitAPIException {
		File directory = new File(destination);

		if (directory.exists())
			throw new RepoDrillerException("Error, destination " + destination + " already exists");

		log.info("Cloning Remote Repository " + url + " into " + this.path);
		Git.cloneRepository()
				.setURI(url)
				.setBare(bare)
				.setDirectory(directory)
				.setCloneAllBranches(true)
				.setNoCheckout(false)
				.call();
	}

	/**
	 * Extract a git repo name from its URL.
	 *
	 * @param url
	 * @return
	 */
	private static String repoNameFromURL(String url) {
		/* Examples:
		 *   git@github.com:substack/node-mkdirp.git
		 *   https://bitbucket.org/fenics-project/notebooks.git
		 */
		int lastSlashIx = url.lastIndexOf("/");

		int lastSuffIx = url.lastIndexOf(URL_SUFFIX);
		if (lastSuffIx < 0)
			lastSuffIx = url.length();

		if (lastSlashIx < 0 || lastSuffIx <= lastSlashIx)
			throw new RepoDrillerException("Error, ill-formed url: " + url);

		return url.substring(lastSlashIx + 1, lastSuffIx);
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
}
