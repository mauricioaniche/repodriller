package org.repodriller.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * Various utilities for working with files.
 *
 * @author Mauricio Aniche
 */
public class FileUtils {

	/**
	 * Get the absolute path to all subdirs of {@code path}.
	 *
	 * @param path	Root of directory tree
	 * @return	All children of this root that are subdirs
	 */
	public static List<String> getAllDirsIn(String path) {
		File dir = new File(path);
		String[] files = dir.list();

		List<String> projects = new ArrayList<String>();
		for (String file : files) {
			File possibleDir = new File(dir, file);
			if (possibleDir.isDirectory()) {
				projects.add(possibleDir.getAbsolutePath());
			}
		}
		return projects;
	}

	/**
	 * Recursively find all the files in this tree, except those in .git and .svn subdirs
	 *
	 * @param pathToLook	Tree root
	 * @return	Every file in this tree for which File.isFile(), except those in
	 */
	public static List<File> getAllFilesInPath(String pathToLook) {
		return getAllFilesInPath(pathToLook, new ArrayList<>());
	}

	/**
	 * Helper for getAllFilesInPath.
	 *
	 * @param pathToLook	Tree root
	 * @param files	Ongoing collection of files (not dirs)
	 * @return	{@code files} plus all files in the tree rooted at pathToLook
	 *          Files that are in .svn or .git dirs are ignored.
	 */
	/* TODO It's strange that a FileUtils class knows abuot .svn and .git.
	 * 		If you want to blacklist subtrees, these should be arguments, not hardcoded in this class.
	 */
	private static List<File> getAllFilesInPath(String pathToLook, List<File> files) {
		for (File f : new File(pathToLook).listFiles()) {
			if (f.isFile())
				files.add(f);
			if (isAProjectSubdirectory(f))
				getAllFilesInPath(f.getAbsolutePath(), files);
		}
		return files;
	}

	/**
	 * Return the raw contents of this file
	 *
	 * @param f	File to read
	 * @return	Contents of file as string
	 */
	public static String readFile(File f) {
		try {
			FileInputStream input = new FileInputStream(f);
			String text = IOUtils.toString(input);
			input.close();
			return text;
		} catch (Exception e) {
			throw new RuntimeException("Error reading file " + f.getAbsolutePath(), e);
		}
	}

	/**
	 * True if f is a directory and doesn't look like svn or git metadata
	 *
	 * @param f
	 * @return True if it looks like a "project subdirectory"
	 */
	private static boolean isAProjectSubdirectory(File f) {
		return f.isDirectory() && !f.getName().equals(".svn") && !f.getName().equals(".git");
	}
}