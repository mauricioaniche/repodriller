package org.repodriller.integration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.repodriller.RepositoryMining;
import org.repodriller.domain.ChangeSet;
import org.repodriller.filter.range.Commits;
import org.repodriller.filter.range.Range;
import org.repodriller.scm.GitRepository;

public class BetweenTagsTest {
	private String path;

	@Before
	public void setUp() throws ParseException {
		this.path = this.getClass().getResource("/").getPath() + "../../test-repos/git-8";
	}
	
	@Test
	public void getCommitsBetween2Revisions() {
		TestVisitor visitor = new TestVisitor();
		
		String from = "tag1";
		String to = "tag2";
		
		new RepositoryMining()
		.in(GitRepository.singleProject(path))
		.through(Commits.betweenTags(from, to))
		.process(visitor)
		.mine();
		
		Assert.assertEquals(3, visitor.getVisitedHashes().size());
		Assert.assertEquals("6bb9e2c6a8080e6b5b34e6e316c894b2ddbf7fcd", visitor.getVisitedHashes().get(0));
		Assert.assertEquals("f1a90b8d7b151ceefd3e3dfc0dc1d0e12b5f48d0", visitor.getVisitedHashes().get(1));
		Assert.assertEquals("4638730126d40716e230c2040751a13153fb1556", visitor.getVisitedHashes().get(2));
	}
}
