package org.repodriller.domain;

public class DiffLine {

	private final int lineNumber;
	private final String line;
	private DiffLineType type;
	
	public DiffLine(int lineNumber, String line, DiffLineType type) {
		this.lineNumber = lineNumber;
		this.line = line;
		this.type = type;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getLine() {
		return line;
	}
	
	public DiffLineType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "DiffLine [lineNumber=" + lineNumber + ", line=" + line + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((line == null) ? 0 : line.hashCode());
		result = prime * result + lineNumber;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (type != other.type)
			return false;
		return true;
	}

}
