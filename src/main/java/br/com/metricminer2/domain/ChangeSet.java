package br.com.metricminer2.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChangeSet {
	
	private final Calendar date;
	private final String id;

	public ChangeSet(String id, Calendar date) {
		this.id = id;
		this.date = date;
		
	}

	public Calendar getTime() {
		return date;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChangeSet other = (ChangeSet) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + id + ", " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime()) + "]";
	}
	

}
