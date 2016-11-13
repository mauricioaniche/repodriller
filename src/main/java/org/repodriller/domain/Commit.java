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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;


public class Commit {

	private String hash;
	private Developer author;
	private Developer committer;
	private String msg;
	private List<Modification> modifications;
	private String parent;
	private Calendar date;
	private Set<String> branches;
	private boolean merge;
	private boolean inMainBranch;
	private TimeZone authorTimeZone;
	private TimeZone committerTimeZone;
	private Calendar committerDate;

	public Commit(String hash, Developer author, Developer committer, Calendar authorDate, Calendar committerDate, String msg, String parent) {
		this(hash, author, committer, authorDate, TimeZone.getDefault(), committerDate, TimeZone.getDefault(), msg, parent, false, new HashSet<>(), false);
	}

	public Commit(String hash, Developer author, Developer committer, Calendar authorDate, TimeZone authorTimeZone, Calendar committerDate, TimeZone committerTimeZone, String msg, String parent, boolean merge, Set<String> branches, boolean isCommitInMainBranch) {
		this.hash = hash;
		this.author = author;
		this.committer = committer;
		this.date = authorDate;
		this.committerDate = committerDate;
		this.msg = msg;
		this.parent = parent;
		this.merge = merge;
		this.authorTimeZone = authorTimeZone;
		this.committerTimeZone = committerTimeZone;
		this.modifications = new ArrayList<Modification>();
		this.branches = branches;
		this.inMainBranch = isCommitInMainBranch;
	}

	public boolean isMerge() {
		return merge;
	}
	
	public String getHash() {
		return hash;
	}

	public Developer getAuthor() {
		return author;
	}

	public String getMsg() {
		return msg;
	}
	
	public Developer getCommitter() {
		return committer;
	}

	public String getParent() {
		return parent;
	}
	
	public void addModification(String oldPath, String newPath, ModificationType change, String diff, String sc) {
		Modification m = new Modification(oldPath, newPath, change, diff, sc);
		modifications.add(m);
		
	}
	
	public void addModifications(List<Modification> modifications) {
		this.modifications.addAll(modifications);
	}
	
	public List<Modification> getModifications() {
		return Collections.unmodifiableList(modifications);
	}
	
	public Calendar getCommitterDate() {
		return committerDate;
	}

	@Override
	public String toString() {
		return "Commit [hash=" + hash + ", parent=" + parent + ", author=" + author + ", msg=" + msg + ", modifications="
				+ modifications + "]";
	}
	
	public TimeZone getAuthorTimeZone() {
		return authorTimeZone;
	}
	
	public TimeZone getCommitterTimeZone() {
		return committerTimeZone;
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
	
	public Calendar getDate() {
		return date;
	}

	public Set<String> getBranches() {
		return Collections.unmodifiableSet(branches);
	}
	
	public boolean isInMainBranch() {
		return inMainBranch;
	}

}
