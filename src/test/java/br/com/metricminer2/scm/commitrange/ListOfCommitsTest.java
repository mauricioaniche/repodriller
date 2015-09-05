package br.com.metricminer2.scm.commitrange;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;

public class ListOfCommitsTest {

	private ListOfCommits range;
	private SCM scm;
	
	@Before
	public void setUp() {
		scm = Mockito.mock(SCM.class);
	}
	
	@Test
	public void should_get_commits_in_range() {
		range = new ListOfCommits(Arrays.asList("2", "3", "4"));
		
		ChangeSet c1 = new ChangeSet("1", new GregorianCalendar(2015, Calendar.JANUARY, 23));
		ChangeSet c2 = new ChangeSet("2", new GregorianCalendar(2015, Calendar.MARCH, 24));
		ChangeSet c3 = new ChangeSet("3", new GregorianCalendar(2015, Calendar.APRIL, 25));
		ChangeSet c4 = new ChangeSet("4", new GregorianCalendar(2014, Calendar.APRIL, 25));
		ChangeSet c5 = new ChangeSet("5", new GregorianCalendar(2013, Calendar.APRIL, 25));
		
		Mockito.when(scm.getChangeSets()).thenReturn(Arrays.asList(c1, c2, c3, c4, c5));
		
		List<ChangeSet> list = range.get(scm);
		
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(c2, list.get(0));
		Assert.assertEquals(c3, list.get(1));
		Assert.assertEquals(c4, list.get(2));
	}
	
}
