package br.com.metricminer2.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class FileUtils {

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

	public static List<File> getAllFilesInPath(String pathToLook, List<File> arquivos) {
		for (File f : new File(pathToLook).listFiles()) {
			if (f.isFile())
				arquivos.add(f);
			if (isAProjectSubdirectory(f))
				getAllFilesInPath(f.getAbsolutePath(), arquivos);
		}
		return arquivos;
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
}
