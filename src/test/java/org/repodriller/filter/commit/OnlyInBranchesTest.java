package org.repodriller.filter.commit;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Developer;

public class OnlyInBranchesTest {

	@Test
	public void shouldAcceptIfCommitBelongsToMainBranch() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");
		
		Calendar d = Calendar.getInstance();
		Commit commitInMain = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), d, TimeZone.getDefault(), d, TimeZone.getDefault(),"x", null, false, branches, true, 0, 0f);
		Commit commitNotInMain = new Commit("123", new Developer("Mau", "mau@mau.com"), new Developer("Mau", "mau@mau.com"), d, TimeZone.getDefault(), d, TimeZone.getDefault(), "x", null, false, branches, false, 0, 0f);

		Assert.assertTrue(new OnlyInMainBranch().accept(commitInMain));
		Assert.assertFalse(new OnlyInMainBranch().accept(commitNotInMain));
	}
}
