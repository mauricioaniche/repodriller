package br.com.metricminer2.filter.commit;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Developer;

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
