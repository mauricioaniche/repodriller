package org.repodriller.integration;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class BetweenDatesTest {

	private String path;

	@Before
	public void setUp() {
		this.path = this.getClass().getResource("/").getPath() + "../../test-repos/git-4";
	}
	
	@Test
	public void shouldFilterByDatesInAscedentOrder() {
		TestVisitor visitor = new TestVisitor();
		
		Calendar from = new GregorianCalendar(2016, Calendar.OCTOBER, 8, 23,0,0);
		Calendar to = new GregorianCalendar(2016, Calendar.OCTOBER, 8, 23,59,0);
		new RepositoryMining()
		.in(GitRepository.singleProject(path))
		.through(Commits.betweenDates(from, to))
		.process(visitor)
		.mine();
		
		Assert.assertEquals(2, visitor.getVisitedHashes().size());
		Assert.assertEquals("a1b6136f978644ff1d89816bc0f2bd86f6d9d7f5", visitor.getVisitedHashes().get(0));
		Assert.assertEquals("375de7a8275ecdc0b28dc8de2568f47241f443e9", visitor.getVisitedHashes().get(1));
	}

	@Test
	public void shouldFilterByDatesInReverseOrder() {
		TestVisitor visitor = new TestVisitor();
		
		Calendar from = new GregorianCalendar(2016, Calendar.OCTOBER, 8, 23,0,0);
		Calendar to = new GregorianCalendar(2016, Calendar.OCTOBER, 8, 23,59,0);
		new RepositoryMining()
		.in(GitRepository.singleProject(path))
		.through(Commits.betweenDates(from, to))
		.reverseOrder()
		.process(visitor)
		.mine();
		
		Assert.assertEquals(2, visitor.getVisitedHashes().size());
		Assert.assertEquals("375de7a8275ecdc0b28dc8de2568f47241f443e9", visitor.getVisitedHashes().get(0));
		Assert.assertEquals("a1b6136f978644ff1d89816bc0f2bd86f6d9d7f5", visitor.getVisitedHashes().get(1));
	}
	
	
}
