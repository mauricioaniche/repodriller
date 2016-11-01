package org.repodriller.domain;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class DiffParserTest {

	@Test
	public void extractLineNumbersAndContentInOldAndNewFile() {
		
		String diff =
			"diff --git a/A b/A\r\n"+
			"index ca17226..ae43afd 100644\r\n"+
			"--- a/A\r\n"+
			"+++ b/A\r\n"+
			"@@ -1,8 +1,8 @@\r\n"+
			"-a\r\n"+
			"-b\r\n"+
			"-c\r\n"+
			"-log.info(\"a\")\r\n"+
			"-d\r\n"+
			"-e\r\n"+
			"-f\r\n"+
			"+aa\r\n"+
			"+bb\r\n"+
			"+cc\r\n"+
			"+log.info(\"aa\")\r\n"+
			"+dd\r\n"+
			"+ee\r\n"+
			"+ff\r\n"+
			" ";
		
		DiffParser parsedDiff = new DiffParser(diff);
		
		List<DiffLine> oldLines = parsedDiff.getBlocks().get(0).getLinesInOldFile();

		Assert.assertTrue(oldLines.contains(new DiffLine(1, "a")));
		Assert.assertTrue(oldLines.contains(new DiffLine(2, "b")));
		Assert.assertTrue(oldLines.contains(new DiffLine(3, "c")));
		Assert.assertTrue(oldLines.contains(new DiffLine(4, "log.info(\"a\")")));
		Assert.assertTrue(oldLines.contains(new DiffLine(5, "d")));
		Assert.assertTrue(oldLines.contains(new DiffLine(6, "e")));
		Assert.assertTrue(oldLines.contains(new DiffLine(7, "f")));
		Assert.assertTrue(oldLines.contains(new DiffLine(8, "")));

		List<DiffLine> newLines = parsedDiff.getBlocks().get(0).getLinesInNewFile();
		
		Assert.assertTrue(newLines.contains(new DiffLine(1, "aa")));
		Assert.assertTrue(newLines.contains(new DiffLine(2, "bb")));
		Assert.assertTrue(newLines.contains(new DiffLine(3, "cc")));
		Assert.assertTrue(newLines.contains(new DiffLine(4, "log.info(\"aa\")")));
		Assert.assertTrue(newLines.contains(new DiffLine(5, "dd")));
		Assert.assertTrue(newLines.contains(new DiffLine(6, "ee")));
		Assert.assertTrue(newLines.contains(new DiffLine(7, "ff")));
		Assert.assertTrue(newLines.contains(new DiffLine(8, "")));
	}

	@Test
	public void onlyAdditions() {
		
		String diff =
				"diff --git a/A b/A\r\n"+
				"index 5ae30ef..04b86b0 100644\r\n"+
				"--- a/A\r\n"+
				"+++ b/A\r\n"+
				"@@ -2,6 +2,7 @@ aa\r\n"+
				" bb\r\n"+
				" cc\r\n"+
				" log.info(\"aa\")\r\n"+
				"+log.debug(\"b\")\r\n"+
				" dd\r\n"+
				" ee\r\n"+
				" ff";
		
		DiffParser parsedDiff = new DiffParser(diff);
		
		List<DiffLine> oldLines = parsedDiff.getBlocks().get(0).getLinesInOldFile();
		Assert.assertTrue(oldLines.contains(new DiffLine(2, "bb")));
		Assert.assertTrue(oldLines.contains(new DiffLine(3, "cc")));
		Assert.assertTrue(oldLines.contains(new DiffLine(4, "log.info(\"aa\")")));
		Assert.assertTrue(oldLines.contains(new DiffLine(5, "dd")));
		Assert.assertTrue(oldLines.contains(new DiffLine(6, "ee")));
		Assert.assertTrue(oldLines.contains(new DiffLine(7, "ff")));

		List<DiffLine> newLines = parsedDiff.getBlocks().get(0).getLinesInNewFile();
		Assert.assertTrue(newLines.contains(new DiffLine(2, "bb")));
		Assert.assertTrue(newLines.contains(new DiffLine(3, "cc")));
		Assert.assertTrue(newLines.contains(new DiffLine(4, "log.info(\"aa\")")));
		Assert.assertTrue(newLines.contains(new DiffLine(5, "log.debug(\"b\")")));
		Assert.assertTrue(newLines.contains(new DiffLine(6, "dd")));
		Assert.assertTrue(newLines.contains(new DiffLine(7, "ee")));
		Assert.assertTrue(newLines.contains(new DiffLine(8, "ff")));
	}

	@Test
	public void onlyDeletions() {
		
		String diff =
				"diff --git a/A b/A\r\n"+
						"index 5ae30ef..04b86b0 100644\r\n"+
						"--- a/A\r\n"+
						"+++ b/A\r\n"+
						"@@ -2,7 +2,6 @@ aa\r\n"+
						" bb\r\n"+
						" cc\r\n"+
						" log.info(\"aa\")\r\n"+
						"-log.debug(\"b\")\r\n"+
						" dd\r\n"+
						" ee\r\n"+
						" ff";
		
		DiffParser parsedDiff = new DiffParser(diff);
		
		List<DiffLine> oldLines = parsedDiff.getBlocks().get(0).getLinesInOldFile();
		Assert.assertTrue(oldLines.contains(new DiffLine(2, "bb")));
		Assert.assertTrue(oldLines.contains(new DiffLine(3, "cc")));
		Assert.assertTrue(oldLines.contains(new DiffLine(4, "log.info(\"aa\")")));
		Assert.assertTrue(oldLines.contains(new DiffLine(5, "log.debug(\"b\")")));
		Assert.assertTrue(oldLines.contains(new DiffLine(6, "dd")));
		Assert.assertTrue(oldLines.contains(new DiffLine(7, "ee")));
		Assert.assertTrue(oldLines.contains(new DiffLine(8, "ff")));
		
		List<DiffLine> newLines = parsedDiff.getBlocks().get(0).getLinesInNewFile();
		Assert.assertTrue(newLines.contains(new DiffLine(2, "bb")));
		Assert.assertTrue(newLines.contains(new DiffLine(3, "cc")));
		Assert.assertTrue(newLines.contains(new DiffLine(4, "log.info(\"aa\")")));
		Assert.assertTrue(newLines.contains(new DiffLine(5, "dd")));
		Assert.assertTrue(newLines.contains(new DiffLine(6, "ee")));
		Assert.assertTrue(newLines.contains(new DiffLine(7, "ff")));
	}

	@Test
	public void additionsAndDeletions() {
		
		String diff =
				"diff --git a/A b/A\r\n"+
				"index 4624afb..870742d 100644\r\n"+
				"--- a/A\r\n"+
				"+++ b/A\r\n"+
				"@@ -1,10 +1,10 @@\r\n"+
				" aa\r\n"+
				" aaa\r\n"+
				"+xxx\r\n"+
				" bb\r\n"+
				" cc\r\n"+
				" log.info(\"aa\")\r\n"+
				" log.debug(\"b\")\r\n"+
				" dd\r\n"+
				" ee\r\n"+
				"-log.trace()\r\n"+
				" ff";
		
		DiffParser parsedDiff = new DiffParser(diff);
		
		List<DiffLine> oldLines = parsedDiff.getBlocks().get(0).getLinesInOldFile();
		Assert.assertTrue(oldLines.contains(new DiffLine(1, "aa")));
		Assert.assertTrue(oldLines.contains(new DiffLine(2, "aaa")));
		Assert.assertTrue(oldLines.contains(new DiffLine(3, "bb")));
		Assert.assertTrue(oldLines.contains(new DiffLine(4, "cc")));
		Assert.assertTrue(oldLines.contains(new DiffLine(5, "log.info(\"aa\")")));
		Assert.assertTrue(oldLines.contains(new DiffLine(6, "log.debug(\"b\")")));
		Assert.assertTrue(oldLines.contains(new DiffLine(7, "dd")));
		Assert.assertTrue(oldLines.contains(new DiffLine(8, "ee")));
		Assert.assertTrue(oldLines.contains(new DiffLine(9, "log.trace()")));
		Assert.assertTrue(oldLines.contains(new DiffLine(10, "ff")));
		
		List<DiffLine> newLines = parsedDiff.getBlocks().get(0).getLinesInNewFile();
		Assert.assertTrue(newLines.contains(new DiffLine(1, "aa")));
		Assert.assertTrue(newLines.contains(new DiffLine(2, "aaa")));
		Assert.assertTrue(newLines.contains(new DiffLine(3, "xxx")));
		Assert.assertTrue(newLines.contains(new DiffLine(4, "bb")));
		Assert.assertTrue(newLines.contains(new DiffLine(5, "cc")));
		Assert.assertTrue(newLines.contains(new DiffLine(6, "log.info(\"aa\")")));
		Assert.assertTrue(newLines.contains(new DiffLine(7, "log.debug(\"b\")")));
		Assert.assertTrue(newLines.contains(new DiffLine(8, "dd")));
		Assert.assertTrue(newLines.contains(new DiffLine(9, "ee")));
		Assert.assertTrue(newLines.contains(new DiffLine(10, "ff")));

	}
	
	@Test
	public void tabsEnters() {
		String diff =
			"diff --git a/A b/A\r\n"+
			"index 708caeb..bdc3fea 100644\r\n"+
			"--- a/A\r\n"+
			"+++ b/A\r\n"+
			"@@ -1,4 +1,17 @@\r\n"+
			" a\r\n"+
			" b\r\n"+
			"-c\r\n"+
			"+\td\r\n"+
			"+cc\r\n"+
			"+\r\n"+
			"+\r\n"+
			"+\r\n"+
			"+\r\n"+
			"+\r\n"+
			"+\r\n"+
			"+\r\n"+
			"+\r\n"+
			"+\r\n"+
			"+\tg\r\n"+
			"+\r\n"+
			"+j\r\n"+
			" ";
		
		DiffParser parsedDiff = new DiffParser(diff);
		
		List<DiffLine> oldLines = parsedDiff.getBlocks().get(0).getLinesInOldFile();
		Assert.assertTrue(oldLines.contains(new DiffLine(1, "a")));
		Assert.assertTrue(oldLines.contains(new DiffLine(2, "b")));
		Assert.assertTrue(oldLines.contains(new DiffLine(3, "c")));
		Assert.assertTrue(oldLines.contains(new DiffLine(4, "")));
		
		List<DiffLine> newLines = parsedDiff.getBlocks().get(0).getLinesInNewFile();
		Assert.assertTrue(newLines.contains(new DiffLine(1, "a")));
		Assert.assertTrue(newLines.contains(new DiffLine(2, "b")));
		Assert.assertTrue(newLines.contains(new DiffLine(3, "\td")));
		Assert.assertTrue(newLines.contains(new DiffLine(4, "cc")));
		Assert.assertTrue(newLines.contains(new DiffLine(5, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(6, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(7, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(8, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(9, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(10, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(11, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(12, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(13, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(14, "\tg")));
		Assert.assertTrue(newLines.contains(new DiffLine(15, "")));
		Assert.assertTrue(newLines.contains(new DiffLine(16, "j")));
	}
	
	@Test
	public void realExampleLargeFileWithMultipleDiffBlocks() {
		String diff = 
				"diff --git a/GitRepository.java b/GitRepository.java\r\n"+
				"index f38a97d..2b96b0e 100644\r\n"+
				"--- a/GitRepository.java\r\n"+
				"+++ b/GitRepository.java\r\n"+
				"@@ -72,7 +72,7 @@ public class GitRepository implements SCM {\r\n"+
				" \r\n"+
				"        private static Logger log = Logger.getLogger(GitRepository.class);\r\n"+
				" \r\n"+
				"-       public GitRepository(String path) {\r\n"+
				"+       public GitRepository2(String path) {\r\n"+
				"                this.path = path;\r\n"+
				"                this.maxNumberFilesInACommit = checkMaxNumberOfFiles();\r\n"+
				"                this.maxSizeOfDiff = checkMaxSizeOfDiff();\r\n"+
				"@@ -155,7 +155,7 @@ public class GitRepository implements SCM {\r\n"+
				"                return git.getRepository().getBranch();\r\n"+
				"        }\r\n"+
				" \r\n"+
				"-       public ChangeSet getHead() {\r\n"+
				"+       public ChangeSet getHead2() {\r\n"+
				"                Git git = null;\r\n"+
				"                try {\r\n"+
				"                        git = openRepository();\r\n"+
				"@@ -320,6 +320,7 @@ public class GitRepository implements SCM {\r\n"+
				" \r\n"+
				"                return diffs;\r\n"+
				"        }\r\n"+
				"+       newline\r\n"+
				" \r\n"+
				"        private void setContext(DiffFormatter df) {\r\n"+
				"                String context = System.getProperty(\"git.diffcontext\");";

		DiffParser parsedDiff = new DiffParser(diff);
		
		List<DiffLine> oldLinesBlock1 = parsedDiff.getBlocks().get(0).getLinesInOldFile();
		Assert.assertTrue(oldLinesBlock1.contains(new DiffLine(72, "")));
		Assert.assertTrue(oldLinesBlock1.contains(new DiffLine(73, "       private static Logger log = Logger.getLogger(GitRepository.class);")));
		Assert.assertTrue(oldLinesBlock1.contains(new DiffLine(74, "")));
		Assert.assertTrue(oldLinesBlock1.contains(new DiffLine(75, "       public GitRepository(String path) {")));
		Assert.assertTrue(oldLinesBlock1.contains(new DiffLine(76, "               this.path = path;")));
		Assert.assertTrue(oldLinesBlock1.contains(new DiffLine(77, "               this.maxNumberFilesInACommit = checkMaxNumberOfFiles();")));
		Assert.assertTrue(oldLinesBlock1.contains(new DiffLine(78, "               this.maxSizeOfDiff = checkMaxSizeOfDiff();")));

		List<DiffLine> newLinesBlock1 = parsedDiff.getBlocks().get(0).getLinesInNewFile();
		Assert.assertTrue(newLinesBlock1.contains(new DiffLine(72, "")));
		Assert.assertTrue(newLinesBlock1.contains(new DiffLine(73, "       private static Logger log = Logger.getLogger(GitRepository.class);")));
		Assert.assertTrue(newLinesBlock1.contains(new DiffLine(74, "")));
		Assert.assertTrue(newLinesBlock1.contains(new DiffLine(75, "       public GitRepository2(String path) {")));
		Assert.assertTrue(newLinesBlock1.contains(new DiffLine(76, "               this.path = path;")));
		Assert.assertTrue(newLinesBlock1.contains(new DiffLine(77, "               this.maxNumberFilesInACommit = checkMaxNumberOfFiles();")));
		Assert.assertTrue(newLinesBlock1.contains(new DiffLine(78, "               this.maxSizeOfDiff = checkMaxSizeOfDiff();")));

		List<DiffLine> oldLinesBlock2 = parsedDiff.getBlocks().get(1).getLinesInOldFile();
		Assert.assertTrue(oldLinesBlock2.contains(new DiffLine(155, "               return git.getRepository().getBranch();")));
		Assert.assertTrue(oldLinesBlock2.contains(new DiffLine(156, "       }")));
		Assert.assertTrue(oldLinesBlock2.contains(new DiffLine(157, "")));
		Assert.assertTrue(oldLinesBlock2.contains(new DiffLine(158, "       public ChangeSet getHead() {")));
		Assert.assertTrue(oldLinesBlock2.contains(new DiffLine(159, "               Git git = null;")));
		Assert.assertTrue(oldLinesBlock2.contains(new DiffLine(160, "               try {")));
		Assert.assertTrue(oldLinesBlock2.contains(new DiffLine(161, "                       git = openRepository();")));

		List<DiffLine> newLinesBlock2 = parsedDiff.getBlocks().get(1).getLinesInNewFile();
		Assert.assertTrue(newLinesBlock2.contains(new DiffLine(155, "               return git.getRepository().getBranch();")));
		Assert.assertTrue(newLinesBlock2.contains(new DiffLine(156, "       }")));
		Assert.assertTrue(newLinesBlock2.contains(new DiffLine(157, "")));
		Assert.assertTrue(newLinesBlock2.contains(new DiffLine(158, "       public ChangeSet getHead2() {")));
		Assert.assertTrue(newLinesBlock2.contains(new DiffLine(159, "               Git git = null;")));
		Assert.assertTrue(newLinesBlock2.contains(new DiffLine(160, "               try {")));
		Assert.assertTrue(newLinesBlock2.contains(new DiffLine(161, "                       git = openRepository();")));
		
		List<DiffLine> oldLinesBlock3 = parsedDiff.getBlocks().get(2).getLinesInOldFile();
		Assert.assertTrue(oldLinesBlock3.contains(new DiffLine(320, "")));
		Assert.assertTrue(oldLinesBlock3.contains(new DiffLine(321, "               return diffs;")));
		Assert.assertTrue(oldLinesBlock3.contains(new DiffLine(322, "       }")));
		Assert.assertTrue(oldLinesBlock3.contains(new DiffLine(323, "")));
		Assert.assertTrue(oldLinesBlock3.contains(new DiffLine(324, "       private void setContext(DiffFormatter df) {")));
		Assert.assertTrue(oldLinesBlock3.contains(new DiffLine(325, "               String context = System.getProperty(\"git.diffcontext\");")));

		
		List<DiffLine> newLinesBlock3 = parsedDiff.getBlocks().get(2).getLinesInNewFile();
		Assert.assertTrue(newLinesBlock3.contains(new DiffLine(320, "")));
		Assert.assertTrue(newLinesBlock3.contains(new DiffLine(321, "               return diffs;")));
		Assert.assertTrue(newLinesBlock3.contains(new DiffLine(322, "       }")));
		Assert.assertTrue(newLinesBlock3.contains(new DiffLine(323, "       newline")));
		Assert.assertTrue(newLinesBlock3.contains(new DiffLine(324, "")));
		Assert.assertTrue(newLinesBlock3.contains(new DiffLine(325, "       private void setContext(DiffFormatter df) {")));
		Assert.assertTrue(newLinesBlock3.contains(new DiffLine(326, "               String context = System.getProperty(\"git.diffcontext\");")));
		
	}
}
