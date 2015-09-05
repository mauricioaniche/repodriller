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

public class SingleCommitTest {

	private SingleCommit range;
	private SCM scm;
	
	@Before
	public void setUp() {
		scm = Mockito.mock(SCM.class);
	}
	
	@Test
	public void should_get_specific_commits() {
		range = new SingleCommit("2");
		
		ChangeSet c1 = new ChangeSet("1", new GregorianCalendar(2015, Calendar.JANUARY, 23));
		ChangeSet c2 = new ChangeSet("2", new GregorianCalendar(2015, Calendar.MARCH, 24));
		ChangeSet c3 = new ChangeSet("3", new GregorianCalendar(2015, Calendar.APRIL, 25));
		
		Mockito.when(scm.getChangeSets()).thenReturn(Arrays.asList(c1, c2, c3));
		
		List<ChangeSet> list = range.get(scm);
		
		Assert.assertEquals(1, list.size());
		Assert.assertEquals(c2, list.get(0));
	}
	
}
