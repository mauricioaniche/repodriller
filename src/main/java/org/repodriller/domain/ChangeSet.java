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

package org.repodriller.domain;

import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

/**
 * A ChangeSet is a POJO for the metadata about a Commit from an SCM.
 * It's "everything but the diff".
 */
public class ChangeSet {

	public enum Contributor {
	    AUTHOR, COMMITTER
	};

	private String id; /* Unique within an SCM. Example: the commit hash. */
	private String message;

	private CommitContributor author; /* Person who committed it. */
	private CommitContributor committer; /* Person who merged it. This is probably the field you want. */

	private Set<String> parentIds; /* Parent(s) of this commit. Only "merge commits" have more than one parent. Does the root commit have 0? */

	private Set<String> branches; /* Set of branches that contain this commit, i.e. the branches for which this commit is an ancestor of the most recent commit in that branch. */
	private boolean inMainBranch; /* True if the main branch is in the set of branches. */

	/**
	 * Minimalist ChangeSet. Many null members. Useful for testing.
	 *
	 * @param id
	 * @param message
	 * @param author
	 */
	public ChangeSet(String id, String message, CommitContributor author) {
		this.id = id;
		this.message = message;
		this.author = author;
		this.committer = this.author;
	}

	/**
	 * Create a ChangeSet. Makes shallow copies of all of the input.
	 *
	 * @param id
	 * @param message
	 * @param author
	 * @param committer
	 * @param parentIds
	 * @param branches
	 * @param inMainBranch
	 */
	public ChangeSet(String id, String message, CommitContributor author, CommitContributor committer,
			Set<String> parentIds, Set<String> branches, boolean inMainBranch) {
		super();
		this.id = id;
		this.message = message;
		this.author = author;
		this.committer = (committer == null) ? author : committer;
		this.parentIds = parentIds;
		this.branches = branches;
		this.inMainBranch = inMainBranch;
	}

	/**
	 * @return The time at which this ChangeSet was created by a developer. In Git, this is "author time", not "committer time".
	 */
	@Deprecated
	public Calendar getTime() {
		return author.time;
	}

	/**
	 * @return Unique ID within SCM
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return Commit message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * How the ChangeSet was created.
	 *
	 * @return Person who authored the commit. See {@link ChangeSet#getCommitter}.
	 */
	public CommitContributor getAuthor() {
		return author;
	}

	/**
	 * How the ChangeSet entered the codebase, e.g. when a PR is accepted.
	 *
	 * @return Person who merged the commit.
	 */
	public CommitContributor getCommitter() {
		return committer;
	}

	/**
	 * Return the requested contributor.
	 *
	 * @param contributor
	 * @return
	 */
	public CommitContributor getContributor(ChangeSet.Contributor contributor) {
		switch (contributor) {
			case AUTHOR:
				return getAuthor();
			case COMMITTER:
				return getCommitter();
			default:
				return null;
		}
	}

	/**
	 * @return Parent(s) of this commit. More than one if it's a "merge commit".
	 */
	public Set<String> getParentIds() {
		return new TreeSet<String>(parentIds);
	}

	/**
	 * @return True if this commit is a "merge commit".
	 */
	public boolean isMerge() {
		return 1 < parentIds.size();
	}

	/**
	 * @return Branches of whose tips this commit is an ancestor.
	 */
	public Set<String> getBranches() {
		return new TreeSet<String>(branches);
	}

	public boolean inMainBranch() {
		return inMainBranch;
	}

	@Override
	public boolean equals(Object obj) {
		/* Boilerplate. */
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		/* Compare two distinct instances. */
		ChangeSet other = (ChangeSet) obj;

		if (id == null) {
			if (other.id != null)
				return false;
		}
		else {
			if (!id.equals(other.id))
				return false;
		}

		return true;
	}

	@Override
	public String toString() {
		if (author.equals(committer)) {
			return String.format("%s: author %s", id, author);
		}
		else {
			return String.format("%s: author %s, committer %s", id, author, committer);
		}
	}

}