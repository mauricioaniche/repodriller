package org.repodriller.domain;

public class DiffLine {

	private final int lineNumber;
	private final String line;
	
	public DiffLine(int lineNumber, String line) {
		this.lineNumber = lineNumber;
		this.line = line;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getLine() {
		return line;
	}

	@Override
	public String toString() {
		return "DiffLine [lineNumber=" + lineNumber + ", line=" + line + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + lineNumber;
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
		DiffLine other = (DiffLine) obj;
		if (line == null) {
			if (other.line != null)
				return false;
		} else if (!line.equals(other.line))
			return false;
		if (lineNumber != other.lineNumber)
			return false;
		return true;
	}
	
	
	
	
	
}
