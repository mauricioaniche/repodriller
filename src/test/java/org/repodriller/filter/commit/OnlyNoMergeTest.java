package org.repodriller.filter.commit;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Developer;
import org.repodriller.filter.commit.OnlyNoMerge;

public class OnlyNoMergeTest {

	@Test
	public void shouldAcceptIfCommitIsNotAMergeCommit() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		
		Commit regularCommit = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, false, branches, true);
		Commit mergeCommit = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, true, branches, true);

		Assert.assertTrue(new OnlyNoMerge().accept(regularCommit));
		Assert.assertFalse(new OnlyNoMerge().accept(mergeCommit));
	}
}
