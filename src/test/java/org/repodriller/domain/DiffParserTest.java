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
		
		List<DiffLine> oldLines = parsedDiff.getLinesInOldFile();

		Assert.assertTrue(oldLines.contains(new DiffLine(1, "a")));
		Assert.assertTrue(oldLines.contains(new DiffLine(2, "b")));
		Assert.assertTrue(oldLines.contains(new DiffLine(3, "c")));
		Assert.assertTrue(oldLines.contains(new DiffLine(4, "log.info(\"a\")")));
		Assert.assertTrue(oldLines.contains(new DiffLine(5, "d")));
		Assert.assertTrue(oldLines.contains(new DiffLine(6, "e")));
		Assert.assertTrue(oldLines.contains(new DiffLine(7, "f")));
		Assert.assertTrue(oldLines.contains(new DiffLine(8, "")));

		List<DiffLine> newLines = parsedDiff.getLinesInNewFile();
		
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
	public void simpleAddition() {
		
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
		
		List<DiffLine> oldLines = parsedDiff.getLinesInOldFile();
		Assert.assertTrue(oldLines.contains(new DiffLine(2, "bb")));
		Assert.assertTrue(oldLines.contains(new DiffLine(3, "cc")));
		Assert.assertTrue(oldLines.contains(new DiffLine(4, "log.info(\"aa\")")));
		Assert.assertTrue(oldLines.contains(new DiffLine(5, "dd")));
		Assert.assertTrue(oldLines.contains(new DiffLine(6, "ee")));
		Assert.assertTrue(oldLines.contains(new DiffLine(7, "ff")));

		List<DiffLine> newLines = parsedDiff.getLinesInNewFile();
		Assert.assertTrue(newLines.contains(new DiffLine(2, "bb")));
		Assert.assertTrue(newLines.contains(new DiffLine(3, "cc")));
		Assert.assertTrue(newLines.contains(new DiffLine(4, "log.info(\"aa\")")));
		Assert.assertTrue(newLines.contains(new DiffLine(5, "log.debug(\"b\")")));
		Assert.assertTrue(newLines.contains(new DiffLine(6, "dd")));
		Assert.assertTrue(newLines.contains(new DiffLine(7, "ee")));
		Assert.assertTrue(newLines.contains(new DiffLine(8, "ff")));
	}
}
