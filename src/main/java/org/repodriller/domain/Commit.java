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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;


public class Commit {
	private ChangeSet metadata;
	private List<Modification> modifications;

	public Commit(ChangeSet cs) {
		this(cs, null);
	}


	public Commit(ChangeSet cs, List<Modification> modifications) {
		this.metadata = cs;
		this.modifications = new ArrayList<Modification>();

		if (modifications != null)
			modifications.forEach(m -> addModification(m));
	}

	public ChangeSet getChangeSet() {
		return metadata;
	}

	public void addModification(Modification m) {
		modifications.add(m);
	}

	public void addModifications(List<Modification> modifications) {
		this.modifications.addAll(modifications);
	}

	public List<Modification> getModifications() {
		return Collections.unmodifiableList(modifications);
	}

	@Override
	public String toString() {
		return "Commit [id=" + metadata.getId() + ", parents=" + metadata.getParentIds() + ", author=" + metadata.getAuthor() + ", msg=" + metadata.getMessage() + ", modifications="
				+ modifications + "]";
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Commit)) {
			return false;
		} else if (other == this) {
			return true;
		} else {
			Commit c = (Commit) other;
			return this.getHash().equals(c.getHash());
		}
	}

	/* Deprecated getters.
	 * Caller should use getChangeSet().X instead. */

	@Deprecated
	public boolean isMerge() {
		return metadata.isMerge();
	}

	@Deprecated
	public String getHash() {
		return getId();
	}

	@Deprecated
	public String getId() {
		return metadata.getId();
	}

	@Deprecated
	public String getMsg() {
		return metadata.getMessage();
	}

	@Deprecated
	public Developer getAuthor() {
		return metadata.getAuthor().asDeveloper();
	}

	@Deprecated
	public Developer getCommitter() {
		return metadata.getCommitter().asDeveloper();
	}

	@Deprecated
	public String getParent() {
		return metadata.getParentIds().iterator().next();
	}

	@Deprecated
	public Set<String> getParents() {
		return metadata.getParentIds();
	}

	@Deprecated
	public boolean isInMainBranch() {
		return metadata.inMainBranch();
	}

	@Deprecated
	public Set<String> getBranches() {
		return metadata.getBranches();
	}

	@Deprecated
	public Calendar getDate() {
		return metadata.getAuthor().time;
	}

	@Deprecated
	public TimeZone getAuthorTimeZone() {
		return metadata.getAuthor().tz;
	}

	@Deprecated
	public Calendar getCommitterDate() {
		return metadata.getCommitter().time;
	}

	@Deprecated
	public TimeZone getCommitterTimeZone() {
		return metadata.getCommitter().tz;
	}
}