package org.repodriller.integration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class BetweenDatesTest {

	private String path;
	private Calendar from;
	private Calendar to;

	@Before
	public void setUp() throws ParseException {
		this.path = this.getClass().getResource("/").getPath() + "../../test-repos/git-4";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
		
		Date d1 = sdf.parse("2016-10-08 17:00:00");
		from = Calendar.getInstance();
		from.setTime(d1);
		
		Date d2 = sdf.parse("2016-10-08 17:59:00");
		to = Calendar.getInstance();
		to.setTime(d2);
	}
	
	@Test
	public void shouldFilterByDatesInAscedentOrder() {
		TestVisitor visitor = new TestVisitor();
		
		
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
