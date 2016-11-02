package org.repodriller.integration;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.commit.OnlyInMainBranch;
import org.repodriller.filter.commit.OnlyModificationsWithFileTypes;
import org.repodriller.filter.commit.OnlyNoMerge;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class CommitFilterIntegrationTest {

	private String pathToRepo4;
	private String pathToRepo5;

	@Before
	public void setUp() {
		this.pathToRepo4 = this.getClass().getResource("/").getPath() + "../../test-repos/git-4";
		this.pathToRepo5 = this.getClass().getResource("/").getPath() + "../../test-repos/git-5";
	}
	
	@Test
	public void shouldFilterCommits() {
		
		TestVisitor visitor = new TestVisitor();
		
		new RepositoryMining()
			.in(GitRepository.singleProject(pathToRepo4))
			.through(Commits.all())
			.filters(new OnlyModificationsWithFileTypes(Arrays.asList("java")))
			.process(visitor)
			.mine();
		
		Assert.assertEquals(2, visitor.getVisitedHashes().size());
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("a1b6136f978644ff1d89816bc0f2bd86f6d9d7f5"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("b8c2be250786975f1c6f47e96922096f1bb25e39"));
	}

	@Test
	public void shouldFilterCommitsUsingManyFilters() {
		
		TestVisitor visitor = new TestVisitor();
		
		new RepositoryMining()
		.in(GitRepository.singleProject(pathToRepo5))
		.through(Commits.all())
		.filters(new OnlyInMainBranch())
		.process(visitor)
		.mine();
		
		Assert.assertEquals(5, visitor.getVisitedHashes().size());
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("4a17f31c0d1285477a3a467d0bc3cb38e775097d"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("ff663cf1931a67d5e47b75fc77dcea432c728052"));
		Assert.assertTrue(visitor.getVisitedHashes().get(2).equals("fa8217c324e7fb46c80e1ddf907f4e141449637e"));
		Assert.assertTrue(visitor.getVisitedHashes().get(3).equals("5d9d79607d7e82b6f236aa29be4ba89a28fb4f15"));
		Assert.assertTrue(visitor.getVisitedHashes().get(4).equals("377e0f474d70f6205784d0150ee0069a050c29ed"));

		visitor = new TestVisitor();
		new RepositoryMining()
		.in(GitRepository.singleProject(pathToRepo5))
		.through(Commits.all())
		.filters(new OnlyInMainBranch(), new OnlyNoMerge())
		.process(visitor)
		.mine();
		
		Assert.assertEquals(4, visitor.getVisitedHashes().size());
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("4a17f31c0d1285477a3a467d0bc3cb38e775097d"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("ff663cf1931a67d5e47b75fc77dcea432c728052"));
		Assert.assertTrue(visitor.getVisitedHashes().get(2).equals("fa8217c324e7fb46c80e1ddf907f4e141449637e"));
		Assert.assertTrue(visitor.getVisitedHashes().get(3).equals("377e0f474d70f6205784d0150ee0069a050c29ed"));
	}

	@Test
	public void shouldVisitAllIfNoFilter() {
		
		TestVisitor visitor = new TestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(pathToRepo4))
		.through(Commits.all())
		.process(visitor)
		.mine();
		
		Assert.assertEquals(3, visitor.getVisitedHashes().size());
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("a1b6136f978644ff1d89816bc0f2bd86f6d9d7f5"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("375de7a8275ecdc0b28dc8de2568f47241f443e9"));
		Assert.assertTrue(visitor.getVisitedHashes().get(2).equals("b8c2be250786975f1c6f47e96922096f1bb25e39"));
	}
	
}
