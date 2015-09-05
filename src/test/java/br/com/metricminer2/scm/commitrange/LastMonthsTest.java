package br.com.metricminer2.scm.commitrange;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.scm.SCM;
import br.com.metricminer2.scm.commitrange.LastMonths;
import org.junit.Assert;

public class LastMonthsTest {

	private LastMonths range;
	private SCM scm;
	
	@Before
	public void setUp() {
		range = new LastMonths(6);
		scm = Mockito.mock(SCM.class);
	}
	
	@Test
	public void should_get_all_commits_until_number_of_months() {
		
		ChangeSet c1 = new ChangeSet("1", new GregorianCalendar(2015, Calendar.JANUARY, 23));
		ChangeSet c2 = new ChangeSet("2", new GregorianCalendar(2015, Calendar.MARCH, 24));
		ChangeSet c3 = new ChangeSet("3", new GregorianCalendar(2015, Calendar.APRIL, 25));
		
		Mockito.when(scm.getChangeSets()).thenReturn(Arrays.asList(c1, c2, c3));
		
		List<ChangeSet> list = range.get(scm);
		
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(c1, list.get(0));
		Assert.assertEquals(c2, list.get(1));
		Assert.assertEquals(c3, list.get(2));
	}
	
	@Test
	public void should_not_all_commits_after_number_of_months() {
		
		ChangeSet c1 = new ChangeSet("1", new GregorianCalendar(2015, Calendar.JANUARY, 23));
		ChangeSet c2 = new ChangeSet("2", new GregorianCalendar(2015, Calendar.MARCH, 24));
		ChangeSet c3 = new ChangeSet("3", new GregorianCalendar(2014, Calendar.APRIL, 25));
		
		Mockito.when(scm.getChangeSets()).thenReturn(Arrays.asList(c1, c2, c3));
		
		List<ChangeSet> list = range.get(scm);
		
		Assert.assertEquals(2, list.size());
		Assert.assertEquals(c1, list.get(0));
		Assert.assertEquals(c2, list.get(1));
	}
}
