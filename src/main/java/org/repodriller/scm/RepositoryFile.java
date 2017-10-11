package org.repodriller.scm;

import java.io.File;

import org.repodriller.util.RDFileUtils;

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
	
	public String getSourceCode() {
		return RDFileUtils.readFile(getFile());
	}
	
	@Override
	public String toString() {
		return "[" + file.getAbsolutePath() + "]";
	}
	
	
	
}
