package org.repodriller.filter.commit;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.domain.CommitContributor;

public class OnlyInBranchesTest {

	@Test
	public void shouldAcceptIfCommitBelongsToMainBranch() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");

		Calendar d = Calendar.getInstance();
		CommitContributor mau = new CommitContributor("Mau", "mau@mau.com", d);

		ChangeSet csInMain = new ChangeSet("123", "foo", mau, null, null, null, true);
		ChangeSet csNotInMain = new ChangeSet("123", "foo", mau, null, null, null, false);
		Commit commitInMain = new Commit(csInMain);
		Commit commitNotInMain = new Commit(csNotInMain);

		Assert.assertTrue(new OnlyInMainBranch().accept(commitInMain));
		Assert.assertFalse(new OnlyInMainBranch().accept(commitNotInMain));
	}
}