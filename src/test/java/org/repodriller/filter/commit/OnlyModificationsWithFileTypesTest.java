package org.repodriller.filter.commit;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Developer;
import org.repodriller.domain.ModificationType;

public class OnlyModificationsWithFileTypesTest {

	@Test
	public void shouldAcceptIfCommitContainsFilesWithThatType() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");
		
		Calendar d = Calendar.getInstance();
		Commit commit = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), d, TimeZone.getDefault(), d, TimeZone.getDefault(), "x", null, false, branches, true, 0, 0f);
		commit.addModification("/dir/File.java", "/dir/File.java", ModificationType.MODIFY, "aaa", "bla");
		commit.addModification("/dir/File.css", "/dir/File.css", ModificationType.MODIFY, "aaa", "bla");

		Assert.assertTrue(new OnlyModificationsWithFileTypes(Arrays.asList(".java")).accept(commit));

		Commit commit2 = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), d, TimeZone.getDefault(), d, TimeZone.getDefault(), "x", null, false, branches, true, 0, 0f);
		commit2.addModification("/dir/File.js", "/dir/File.js", ModificationType.MODIFY, "aaa", "bla");
		commit2.addModification("/dir/File.css", "/dir/File.css", ModificationType.MODIFY, "aaa", "bla");
		Assert.assertFalse(new OnlyModificationsWithFileTypes(Arrays.asList(".java")).accept(commit2));
	}
}
