package org.repodriller.filter.range;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
public class NumOfCommitsTest {
    private NumOfCommits range;
    private SCM scm;

    @Before
    public void setUp() {
        scm = Mockito.mock(SCM.class);
    }

    @Test
    public void should_get_commits_in_range() {
        range = new NumOfCommits(5);

        ChangeSet c1 = new ChangeSet("1", new GregorianCalendar(2015, Calendar.JANUARY, 23));
        ChangeSet c2 = new ChangeSet("2", new GregorianCalendar(2015, Calendar.MARCH, 24));
        ChangeSet c3 = new ChangeSet("3", new GregorianCalendar(2016, Calendar.APRIL, 25));
        ChangeSet c4 = new ChangeSet("4", new GregorianCalendar(2016, Calendar.APRIL, 25));
        ChangeSet c5 = new ChangeSet("5", new GregorianCalendar(2017, Calendar.APRIL, 25));
        ChangeSet c6 = new ChangeSet("6", new GregorianCalendar(2017, Calendar.APRIL, 26));
        ChangeSet c7 = new ChangeSet("7", new GregorianCalendar(2017, Calendar.MAY, 27));
        ChangeSet c8 = new ChangeSet("8", new GregorianCalendar(2017, Calendar.MAY, 28));
        ChangeSet c9 = new ChangeSet("9", new GregorianCalendar(2017, Calendar.JUNE, 29));
        ChangeSet c10 = new ChangeSet("10", new GregorianCalendar(2017, Calendar.JUNE, 30));

        Mockito.when(scm.getChangeSets()).thenReturn(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8 , c9, c10));

        List<ChangeSet> list = range.get(scm);
        Assert.assertEquals(5, list.size());
        Assert.assertTrue(list.contains(c1));
        Assert.assertTrue(list.contains(c2));
        Assert.assertTrue(list.contains(c3));
        Assert.assertTrue(list.contains(c4));
        Assert.assertTrue(list.contains(c5));

        range = new NumOfCommits(1);
        List<ChangeSet> list1 = range.get(scm);
        Assert.assertEquals(1, list1.size());
        Assert.assertTrue(list1.contains(c1));

    }
}
