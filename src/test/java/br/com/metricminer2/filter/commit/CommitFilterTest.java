package br.com.metricminer2.filter.commit;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Developer;

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
