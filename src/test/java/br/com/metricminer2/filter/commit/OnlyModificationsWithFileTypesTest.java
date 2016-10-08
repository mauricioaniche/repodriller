package br.com.metricminer2.filter.commit;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Developer;
import br.com.metricminer2.domain.ModificationType;

public class OnlyModificationsWithFileTypesTest {

	@Test
	public void shouldAcceptIfCommitContainsFilesWithThatType() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");
		
		Commit commit = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, false, branches, true);
		commit.addModification("/dir/File.java", "/dir/File.java", ModificationType.MODIFY, "aaa", "bla");
		commit.addModification("/dir/File.css", "/dir/File.css", ModificationType.MODIFY, "aaa", "bla");

		Assert.assertTrue(new OnlyModificationsWithFileTypes(Arrays.asList(".java")).accept(commit));

		Commit commit2 = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, false, branches, true);
		commit2.addModification("/dir/File.js", "/dir/File.js", ModificationType.MODIFY, "aaa", "bla");
		commit2.addModification("/dir/File.css", "/dir/File.css", ModificationType.MODIFY, "aaa", "bla");
		Assert.assertFalse(new OnlyModificationsWithFileTypes(Arrays.asList(".java")).accept(commit2));
	}
}
