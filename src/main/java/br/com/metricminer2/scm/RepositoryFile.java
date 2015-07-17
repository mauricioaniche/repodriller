package br.com.metricminer2.scm;

import java.io.File;

public class RepositoryFile {

	private File file;

	public RepositoryFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public boolean fileNameEndsWith(String suffix) {
		return file.getName().toLowerCase().endsWith(suffix.toLowerCase());
	}

	public boolean fileNameMatches(String regex) {
		return file.getName().toLowerCase().matches(regex);
	}

	public String getFullName() {
		return file.getAbsolutePath();
	}

	public boolean fileNameContains(String text) {
		return file.getName().toLowerCase().contains(text);
	}
	
	@Override
	public String toString() {
		return "[" + file.getAbsolutePath() + "]";
	}
	
	
	
}
