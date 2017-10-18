package org.repodriller.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A POJO to track details about a person involved with a commit.
 */
public class CommitPerson {
	public String name;
	public String email;
	public Calendar time;

	public CommitPerson(String author, String email, Calendar time) {
		this.name = author;
		this.email = email;
		this.time = time;
	}

	public CommitPerson(CommitPerson c) {
		this.name = c.name;
		this.email = c.email;
		this.time = c.time;
	}

	@Override
	public String toString() {
		return String.format("%s <%s> at %s",  name, email, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
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
		CommitPerson other = (CommitPerson) obj;

		if (name == null) {
			if (other.name != null)
				return false;
		}
		else {
			if (!name.equals(other.name))
				return false;
		}

		if (email == null) {
			if (other.email != null)
				return false;
		}
		else {
			if (!email.equals(other.name))
				return false;
		}

		if (time == null) {
			if (other.time != null)
				return false;
		}
		else {
			if (!time.equals(other.name))
				return false;
		}

		return true;
	}
}