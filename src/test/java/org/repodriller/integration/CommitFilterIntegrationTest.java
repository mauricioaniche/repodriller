package org.repodriller.integration;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.commit.OnlyModificationsWithFileTypes;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

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
		
		Assert.assertEquals(2, visitor.getVisitedHashes().size());
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("a1b6136f978644ff1d89816bc0f2bd86f6d9d7f5"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("b8c2be250786975f1c6f47e96922096f1bb25e39"));
	}

	@Test
	public void shouldVisitAllIfNoFilter() {
		
		TestVisitor visitor = new TestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(path))
		.through(Commits.all())
		.process(visitor)
		.mine();
		
		Assert.assertEquals(3, visitor.getVisitedHashes().size());
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("a1b6136f978644ff1d89816bc0f2bd86f6d9d7f5"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("375de7a8275ecdc0b28dc8de2568f47241f443e9"));
		Assert.assertTrue(visitor.getVisitedHashes().get(2).equals("b8c2be250786975f1c6f47e96922096f1bb25e39"));
	}
	
}
