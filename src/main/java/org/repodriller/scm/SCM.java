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

import java.nio.file.Path;
import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Modification;

/**
 * This interface defines interactions with a source code repository that uses a Source Code Management system (i.e. version control system).
 * In essence, any SCM consists of a set of commits in a possibly-linear directed acyclic graph (DAG).
 *
 * An SCM:
 *  - Maintains a set of Commits (full details) that are uniquely identified with ChangeSets (metadata)
 *  - Has a "most recent commit" called its head
 *  - Can roll back the state of the repository to a particular Commit
 *  - Can return to the head
 *
 * @author Mauricio Aniche
 */
public interface SCM {

	/* Methods for general information about the SCM. */

	/**
	 * @return Total commits in this SCM.
	 */
	long totalCommits();

	/**
	 * @return ChangeSet representing the "head" (most recent) commit.
	 */
	ChangeSet getHead();

	/**
	 * @return All ChangeSets in this SCM.
	 */
	List<ChangeSet> getChangeSets();

	/**
	 * @return Metadata about this SCM.
	 */
	SCMRepository info();

	/* Methods for retrieving Commits. */

	/**
	 * Retrieve the Commit with this id.
	 *
	 * @param id	The commit to retrieve
	 * @return	The Commit with this id, or null.
	 */
	Commit getCommit(String id);
	/* TODO A method named getCommitXYZ should return a Commit. */
	String getCommitFromTag(String tag);

	/**
	 * Get the diff between the specified commits.
	 *
	 * @param priorCommit	The first (old) commit
	 * @param laterCommit	The second (new) commit
	 * @return A list of Modification objects representing the changes between
	 * 			priorCommit and laterCommit.
	 */
	List<Modification> getDiffBetweenCommits(String priorCommit, String laterCommit);

	@Deprecated
	String blame(String file, String currentCommit, Integer line);
	List<BlamedLine> blame(String file, String commitToBeBlamed, boolean priorCommit);

	/* Methods for interacting with current repo state. */

	/**
	 * Return the repo to the state immediately following the application of the Commit identified by this id.
	 * @param id	The commit to checkout.
	 * Implementors: May not be thread safe, consider synchronized.
	 */
	void checkout(String id);

	/**
	 * Return the repo to the state of the head commit.
	 * Implementors: May not be thread safe, consider synchronized.
	 */
	void reset();

	/**
	 * @return All files currently in the repo.
	 * Implementors: May not be thread safe, consider synchronized.
	 */
	List<RepositoryFile> files();

	/**
	 * Duplicate this SCM.
	 *
	 * @param dest On-disk records will be rooted here (e.g. "/tmp/clone-here")
	 * @returns An SCM corresponding to the copy
	 */
	SCM clone(Path dest);

	/**
	 * Delete any local storage devoted to this SCM.
	 * Should be safe to call repeatedly without ill effect.
	 */
	void delete();

	/**
	 * Configure which data should be extracted from the repository.
	 * (usually for performance reasons)
	 *
	 * Default should be to collect *everything*
	 */
	void setDataToCollect(CollectConfiguration config);
}
