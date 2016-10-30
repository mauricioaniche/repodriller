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

public class OnlyInMainBranchTest {

	@Test
	public void shouldAcceptIfCommitBelongsToAnyOfBranchesInTheList() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");
		Commit commit = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, false, branches, false);

		Assert.assertTrue(new OnlyInBranches(Arrays.asList("master")).accept(commit));
		Assert.assertTrue(new OnlyInBranches(Arrays.asList("b1")).accept(commit));
		Assert.assertFalse(new OnlyInBranches(Arrays.asList("b2")).accept(commit));
	}
}
