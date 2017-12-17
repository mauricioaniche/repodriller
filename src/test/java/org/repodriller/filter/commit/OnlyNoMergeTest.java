package org.repodriller.filter.commit;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.domain.CommitContributor;

public class OnlyNoMergeTest {

	@Test
	public void shouldAcceptIfCommitIsNotAMergeCommit() {
		Set<String> branches = new HashSet<>();
		branches.add("master");

		Calendar d = Calendar.getInstance();
		CommitContributor mau = new CommitContributor("Mau", "mau@mau.com", d);

		TreeSet<String> oneParent = new TreeSet<String>(Arrays.asList("oneParent"));
		TreeSet<String> twoParents = new TreeSet<String>(Arrays.asList("oneParent", "twoParent"));

		ChangeSet merge = new ChangeSet("123", "foo", mau, null, oneParent, null, false);
		ChangeSet notMerge = new ChangeSet("456", "bar", mau, null, twoParents, null, false);
		Commit regularCommit = new Commit(merge);
		Commit mergeCommit = new Commit(notMerge);

		Assert.assertTrue(new OnlyNoMerge().accept(regularCommit));
		Assert.assertFalse(new OnlyNoMerge().accept(mergeCommit));
	}
}
