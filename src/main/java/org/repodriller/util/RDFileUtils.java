package org.repodriller.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class RDFileUtils {

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
	public static List<File> getAllFilesInPath(String pathToLook) {
		return getAllFilesInPath(pathToLook, new ArrayList<>());
	}

	private static List<File> getAllFilesInPath(String pathToLook, List<File> files) {
		for (File f : new File(pathToLook).listFiles()) {
			if (f.isFile())
				files.add(f);
			if (isAProjectSubdirectory(f))
				getAllFilesInPath(f.getAbsolutePath(), files);
		}
		return files;
	}


	public static String readFile(File f) {
		try {
			FileInputStream input = new FileInputStream(f);
			String text = IOUtils.toString(input);
			input.close();
			return text;
		} catch (Exception e) {
			throw new RuntimeException("error reading file " + f.getAbsolutePath(), e);
		}
	}


	private static boolean isAProjectSubdirectory(File f) {
		return f.isDirectory() && !f.getName().equals(".svn") && !f.getName().equals(".git");
	}

	/**
	 * Get a unique path that does not yet exist
	 *
	 * @param directory	Where to begin the path, defaults to the system temp dir
	 * @return	Absolute path to an available file
	 * @throws IOException
	 */
	public static String getTempPath(String directory) throws IOException {
		try {
			if (directory == null)
				directory = FileUtils.getTempDirectoryPath();

			File dir = new File(directory);
			dir.mkdirs();

			File tmpFile = File.createTempFile("RD-", "", dir);
			String absPath = tmpFile.getAbsolutePath();
			tmpFile.delete();
			return absPath;
		} catch (IOException e) {
			throw new IOException("Error, couldn't create temp dir in " + directory + ": " + e);
		}
	}
}
