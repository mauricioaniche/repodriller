package br.com.metricminer2.domain;


public class Modification {

	private String oldPath;
	private String newPath;
	private ModificationType type;
	private String diff;
	private String sourceCode;

	public Modification(String oldPath, String newPath, ModificationType type, String diff, String sourceCode) {
		this.oldPath = oldPath;
		this.newPath = newPath;
		this.type = type;
		this.diff = diff;
		this.sourceCode = sourceCode;
	}

	public String getOldPath() {
		return oldPath;
	}

	public String getNewPath() {
		return newPath;
	}

	public ModificationType getType() {
		return type;
	}

	public String getDiff() {
		return diff;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	@Override
	public String toString() {
		return "Modification [oldPath=" + oldPath + ", newPath=" + newPath + ", type=" + type
				+ "]";
	}

	public boolean wasDeleted() {
		return !type.equals(ModificationType.DELETE);
	}
	
	

	
}
