package org.repodriller.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * A POJO to track details about a person involved with a commit.
 */
public class CommitContributor {
	public String name;
	public String email;
	public Calendar time;
	public TimeZone tz;

	public CommitContributor(String name, String email, Calendar time) {
		this(name, email, time, null);
	}

	public CommitContributor(String name, String email, Calendar time, TimeZone tz) {
		this.name = name;
		this.email = email;
		this.time = time;
		this.tz = tz;
	}

	public CommitContributor(CommitContributor c) {
		this.name = c.name;
		this.email = c.email;
		this.time = c.time;
		this.tz = c.tz;
	}

	@Override
	public String toString() {
		return String.format("%s <%s> at %s",  name, email, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
	}

	public Developer asDeveloper() {
		return new Developer(name, email);
	}

	@Override
	public int hashCode() {
		final int prime1 = 17;
		final int prime2 = 31;

		int hash = prime1;

		/* I'm sure there's a more efficient way to do this... */
		List<Object> members = new ArrayList<Object>();
		members.add(name);
		members.add(email);
		members.add(time);
		members.add(tz);

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
		CommitContributor other = (CommitContributor) obj;

		/* I'm sure there's a more efficient way to do this... */
		List<Object> myMembers = new ArrayList<Object>();
		myMembers.add(name);
		myMembers.add(email);
		myMembers.add(time);
		myMembers.add(tz);

		List<Object> theirMembers = new ArrayList<Object>();
		theirMembers.add(other.name);
		theirMembers.add(other.email);
		theirMembers.add(other.time);
		theirMembers.add(other.tz);

		for (int i = 0; i < myMembers.size(); i++) {
			Object mine = myMembers.get(i);
			Object theirs = theirMembers.get(i);

			if (mine == null ^ theirs == null) // XOR: Both must be null or both must be not null.
				return false;
			if (mine != null && !mine.equals(theirs)) // Not null, so check equality.
				return false;
		}

		return true;
	}
}