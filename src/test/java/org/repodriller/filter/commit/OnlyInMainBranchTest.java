package org.repodriller.filter.commit;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.domain.CommitContributor;

public class OnlyInMainBranchTest {

	@Test
	public void shouldAcceptIfCommitBelongsToAnyOfBranchesInTheList() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");
		Calendar d = Calendar.getInstance();
		CommitContributor mau = new CommitContributor("Mau", "mau@mau.com", d);
		ChangeSet cs = new ChangeSet("123", "foo", mau, null, null, branches, true);
		Commit commit = new Commit(cs);

		Assert.assertTrue(new OnlyInBranches(Arrays.asList("master")).accept(commit));
		Assert.assertTrue(new OnlyInBranches(Arrays.asList("b1")).accept(commit));
		Assert.assertFalse(new OnlyInBranches(Arrays.asList("b2")).accept(commit));
	}
}
