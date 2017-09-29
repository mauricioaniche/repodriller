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
 *
 * @author Mauricio Aniche
 */
public interface CommitVisitor {

	/**
	 * Called before this CommitVisitor begins visiting {@code repo}.
	 *
	 * @param repo
	 * @param writer
	 */
	default void initialize(SCMRepository repo, PersistenceMechanism writer) {}

	/**
	 * Visit this <{@code repo}, {@code commit}> combination.
	 * Should be thread-safe.
	 *
	 * @param repo
	 * @param commit
	 * @param writer
	 */
	void process(SCMRepository repo, Commit commit, PersistenceMechanism writer);

	/**
	 * Called after this CommitVisitor has completely visited {@code repo}.
	 *
	 * @param repo
	 * @param writer
	 */
	default void finalize(SCMRepository repo, PersistenceMechanism writer) {}

	/**
	 * @return	Name of this CommitVisitor
	 */
	default String name() { return this.getClass().getSimpleName(); }
}
