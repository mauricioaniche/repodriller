package org.repodriller.filter.commit;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Developer;
import org.repodriller.filter.commit.OnlyInMainBranch;

public class OnlyInBranchesTest {

	@Test
	public void shouldAcceptIfCommitBelongsToMainBranch() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");
		
		Commit commitInMain = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, false, branches, true);
		Commit commitNotInMain = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), Calendar.getInstance(), "x", null, false, branches, false);

		Assert.assertTrue(new OnlyInMainBranch().accept(commitInMain));
		Assert.assertFalse(new OnlyInMainBranch().accept(commitNotInMain));
	}
}
