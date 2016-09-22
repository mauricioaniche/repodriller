package br.com.metricminer2.scm;

public class BlamedLine {

	private final int lineNumber;
	private final String line;
	private final String author;
	private final String committer;
	private final String commit;
	
	public BlamedLine(int lineNumber, String line, String author, String committer, String commit) {
		this.lineNumber = lineNumber;
		this.line = line;
		this.author = author;
		this.committer = committer;
		this.commit = commit;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getLine() {
		return line;
	}

	public String getAuthor() {
		return author;
	}

	public String getCommitter() {
		return committer;
	}

	public String getCommit() {
		return commit;
	}

	@Override
	public String toString() {
		return "BlamedLine [lineNumber=" + lineNumber + ", line=" + line + ", author=" + author + ", committer="
				+ committer + ", commit=" + commit + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((commit == null) ? 0 : commit.hashCode());
		result = prime * result + ((committer == null) ? 0 : committer.hashCode());
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
		BlamedLine other = (BlamedLine) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (commit == null) {
			if (other.commit != null)
				return false;
		} else if (!commit.equals(other.commit))
			return false;
		if (committer == null) {
			if (other.committer != null)
				return false;
		} else if (!committer.equals(other.committer))
			return false;
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
