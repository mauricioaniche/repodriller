package org.repodriller.filter.range;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.CommitContributor;
import org.repodriller.scm.SCM;

public class BetweenDatesTest {

	private BetweenDates range;
	private SCM scm;

	@Before
	public void setUp() {
		scm = Mockito.mock(SCM.class);
	}

	@Test
	public void should_get_commits_in_range() {
		List<ChangeSet> list;

		ChangeSet c1 = new ChangeSet("1", "", new CommitContributor("", "", new GregorianCalendar(2015, Calendar.JANUARY, 23)));
		ChangeSet c2 = new ChangeSet("2", "", new CommitContributor("", "", new GregorianCalendar(2015, Calendar.MARCH, 24)));
		ChangeSet c3 = new ChangeSet("3", "", new CommitContributor("", "", new GregorianCalendar(2016, Calendar.APRIL, 25)));
		ChangeSet c4 = new ChangeSet("4", "", new CommitContributor("", "", new GregorianCalendar(2016, Calendar.APRIL, 25)));
		ChangeSet c5 = new ChangeSet("5", "", new CommitContributor("", "", new GregorianCalendar(2017, Calendar.APRIL, 25)));

		Mockito.when(scm.getChangeSets()).thenReturn(Arrays.asList(c1, c2, c3, c4, c5));

		/* Entries between Jan 1 2016 and Dec 31 2016. */
		range = new BetweenDates(new GregorianCalendar(2016, Calendar.JANUARY, 01), new GregorianCalendar(2016, Calendar.DECEMBER, 31));

		list = range.get(scm);
		Assert.assertEquals(2, list.size());
		Assert.assertTrue(list.contains(c3));
		Assert.assertTrue(list.contains(c4));

		/* Entries before Dec 31 2016. */
		range = new BetweenDates(null, new GregorianCalendar(2016, Calendar.DECEMBER, 31));

		list = range.get(scm);
		Assert.assertEquals(4, list.size());
		Assert.assertTrue(list.contains(c1));
		Assert.assertTrue(list.contains(c2));
		Assert.assertTrue(list.contains(c3));
		Assert.assertTrue(list.contains(c4));

		/* Entries after Dec 31 2016. */
		range = new BetweenDates(new GregorianCalendar(2016, Calendar.DECEMBER, 31), null);

		list = range.get(scm);
		Assert.assertEquals(1, list.size());
		Assert.assertTrue(list.contains(c5));

		/* All entries. */
		range = new BetweenDates(null, null);

		list = range.get(scm);
		Assert.assertEquals(5, list.size());
		Assert.assertTrue(list.contains(c1));
		Assert.assertTrue(list.contains(c2));
		Assert.assertTrue(list.contains(c3));
		Assert.assertTrue(list.contains(c4));
		Assert.assertTrue(list.contains(c5));
	}

}
