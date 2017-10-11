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
import org.repodriller.domain.Modification;
import org.repodriller.domain.ModificationType;
import org.repodriller.filter.commit.OnlyModificationsWithFileTypes;

public class OnlyModificationsWithFileTypesTest {

	@Test
	public void shouldAcceptIfCommitContainsFilesWithThatType() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");
		
		Calendar d = Calendar.getInstance();
		Commit commit = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), d, TimeZone.getDefault(), d, TimeZone.getDefault(), "x", null, false, branches, true);
		commit.addModification(new Modification("/dir/File.java", "/dir/File.java", ModificationType.MODIFY, "aaa", "bla"));
		commit.addModification(new Modification("/dir/File.css", "/dir/File.css", ModificationType.MODIFY, "aaa", "bla"));

		Assert.assertTrue(new OnlyModificationsWithFileTypes(Arrays.asList(".java")).accept(commit));

		Commit commit2 = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), d, TimeZone.getDefault(), d, TimeZone.getDefault(), "x", null, false, branches, true);
		commit2.addModification(new Modification("/dir/File.js", "/dir/File.js", ModificationType.MODIFY, "aaa", "bla"));
		commit2.addModification(new Modification("/dir/File.css", "/dir/File.css", ModificationType.MODIFY, "aaa", "bla"));
		Assert.assertFalse(new OnlyModificationsWithFileTypes(Arrays.asList(".java")).accept(commit2));
	}
}
