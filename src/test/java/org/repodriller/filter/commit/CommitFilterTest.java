package org.repodriller.filter.commit;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Developer;
import org.repodriller.filter.commit.OnlyInBranches;
import org.repodriller.filter.commit.OnlyInMainBranch;

public class CommitFilterTest {

	@Test
	public void testDecorator() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");
		Commit commit = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, false, branches, true);
		Commit commit2 = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, false, branches, false);
		
		OnlyInBranches filter = new OnlyInBranches(Arrays.asList("master"), new OnlyInMainBranch());
		Assert.assertTrue(filter.accept(commit));
		Assert.assertFalse(filter.accept(commit2));
	}
}
