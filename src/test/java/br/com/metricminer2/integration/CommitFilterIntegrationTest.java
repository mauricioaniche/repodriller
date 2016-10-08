package br.com.metricminer2.integration;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.RepositoryMining;
import br.com.metricminer2.filter.commit.OnlyModificationsWithFileTypes;
import br.com.metricminer2.filter.range.Commits;
import br.com.metricminer2.scm.GitRepository;

public class CommitFilterIntegrationTest {

	private String path;

	@Before
	public void setUp() {
		this.path = this.getClass().getResource("/").getPath() + "../../test-repos/git-4";
	}
	
	@Test
	public void shouldFilterCommits() {
		
		TestVisitor visitor = new TestVisitor();
		
		new RepositoryMining()
			.in(GitRepository.singleProject(path))
			.through(Commits.all())
			.withCommits(new OnlyModificationsWithFileTypes(Arrays.asList("java")))
			.process(visitor)
			.mine();
		
		Assert.assertEquals(2, visitor.getVisitedCommits().size());
		Assert.assertTrue(visitor.getVisitedCommits().get(0).equals("b8c2be250786975f1c6f47e96922096f1bb25e39"));
		Assert.assertTrue(visitor.getVisitedCommits().get(1).equals("a1b6136f978644ff1d89816bc0f2bd86f6d9d7f5"));
	}

	@Test
	public void shouldVisitAllIfNoFilter() {
		
		TestVisitor visitor = new TestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(path))
		.through(Commits.all())
		.process(visitor)
		.mine();
		
		Assert.assertEquals(3, visitor.getVisitedCommits().size());
		Assert.assertTrue(visitor.getVisitedCommits().get(0).equals("b8c2be250786975f1c6f47e96922096f1bb25e39"));
		Assert.assertTrue(visitor.getVisitedCommits().get(1).equals("375de7a8275ecdc0b28dc8de2568f47241f443e9"));
		Assert.assertTrue(visitor.getVisitedCommits().get(2).equals("a1b6136f978644ff1d89816bc0f2bd86f6d9d7f5"));
	}
	
}
