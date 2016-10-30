package org.repodriller.integration;

import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class DatesAndTimezonesTest {

	private String path4;
	private String path2;

	@Before
	public void setUp() {
		this.path4 = this.getClass().getResource("/").getPath() + "../../test-repos/git-4";
		this.path2 = this.getClass().getResource("/").getPath() + "../../test-repos/git-2";
	}
	
	@Test
	public void oneTimezone() {
		TestVisitor visitor = new TestVisitor();
		
		new RepositoryMining()
		.in(GitRepository.singleProject(path4))
		.through(Commits.single("375de7a8275ecdc0b28dc8de2568f47241f443e9"))
		.process(visitor)
		.mine();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
		sdf.setTimeZone(visitor.getVisitedCommits().get(0).getAuthorTimeZone());
		String time = sdf.format(visitor.getVisitedCommits().get(0).getDate().getTime());
		Assert.assertEquals("08/10/2016 17:57:49 GMT-04:00", time);
	}

	@Test
	public void otherTimezone() {
		TestVisitor visitor = new TestVisitor();
		
		new RepositoryMining()
		.in(GitRepository.singleProject(path2))
		.through(Commits.single("29e929fbc5dc6a2e9c620069b24e2a143af4285f"))
		.process(visitor)
		.mine();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
		sdf.setTimeZone(visitor.getVisitedCommits().get(0).getAuthorTimeZone());
		String time = sdf.format(visitor.getVisitedCommits().get(0).getDate().getTime());
		Assert.assertEquals("04/04/2016 13:21:25 GMT+02:00", time);
	}

}
