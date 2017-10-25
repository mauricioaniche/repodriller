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

import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;

/**
 * A CommitVisitor visits Commits from an SCMRepository.
 * Life cycle:
 * For all SCMRepositories being handled by RepositoryMining:
 *   - {@link CommitVisitor#initialize} against this SCMRepository
 *   - {@link CommitVisitor#process} all the Commits
 *   - {@link CommitVisitor#finalize} this SCMRepository
 *
 * Notes:
 *   1. If {@link org.repodriller.RepositoryMining#withThreads} is being used, then process() must be thread-safe.
 *   2. Multiple CommitVisitors may be operating concurrently on this SCMRepository.
 *   3. A CommitVisitor will only visit commits from one SCMRepository at a time.
 *   4. The SCMRepository passed to initialize() and finalize() will be the same.
 *      However, process() may receive different equivalent SCMRepository's.
 *
 * @author Mauricio Aniche
 */
public interface CommitVisitor {

	/**
	 * Called before this CommitVisitor begins visiting {@code repo}.
	 *
	 * @param repo The SCM repo
	 * @param writer The persistence mechanism
	 */
	default void initialize(SCMRepository repo, PersistenceMechanism writer) {}

	/**
	 * Visit this <{@code repo}, {@code commit}> combination.
	 * May need to be thread-safe, see class notes.
	 * {@code repo} may not be the repo object that is passed to initialize and finalize,
	 * but it is always that object or an equivalent clone thereof.
	 *
	 * @param repo The SCM repo
	 * @param commit The visited commit
	 * @param writer The persistence mechanism
	 */
	void process(SCMRepository repo, Commit commit, PersistenceMechanism writer);

	/**
	 * Called after this CommitVisitor has completely visited {@code repo}.
	 *
	 * @param repo The SCM repo
	 * @param writer The persistence mechanism
	 */
	default void finalize(SCMRepository repo, PersistenceMechanism writer) {}

	/**
	 * @return	Name of this CommitVisitor
	 */
	default String name() { return this.getClass().getSimpleName(); }
}
