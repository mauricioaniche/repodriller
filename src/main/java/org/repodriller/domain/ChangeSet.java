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

import java.util.ArrayList;
import java.util.List;

/**
 * A ChangeSet is metadata about a Commit from an SCM.
 * It's "everything but the diff".
 */
public class ChangeSet {

	private String id; /* Unique within an SCM. */
	private String message;

	private CommitPerson author; /* Person who committed it. */
	private CommitPerson committer; /* Person who merged it. This is probably the field you want. */

	public ChangeSet(String id, String message, CommitPerson author) {
		this.id = id;
		this.message = message;

		this.author = author;
		this.committer = this.author;
	}

	public ChangeSet(String id, String message, CommitPerson author, CommitPerson committer) {
		this.id = id;
		this.message = message;

		this.author = author;
		this.committer = committer;
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
	public CommitPerson getAuthor() {
		return author;
	}

	/**
	 * How the ChangeSet entered the codebase, e.g. when a PR is accepted.
	 *
	 * @return Person who merged the commit.
	 */
	public CommitPerson getCommitter() {
		return committer;
	}

	@Override
	public int hashCode() {
		final int prime1 = 17;
		final int prime2 = 31;

		int hash = prime1;

		/* I'm sure there's a more efficient way to do this... */
		List<Object> members = new ArrayList<Object>();
		members.add(id);
		members.add(message);
		members.add(author);
		members.add(committer);

		for (Object member : members) {
			hash = prime2*hash + ((member == null) ? 0 : member.hashCode());
		}

		return hash;
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