package org.repodriller.integration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class FirstParentOnlyTest {

	private String path;

	@Before
	public void setUp() {
		this.path = this.getClass().getResource("/").getPath() + "../../test-repos/git-6";
	}
	
	@Test
	public void firstParentsOnly() {
		TestVisitor visitor = new TestVisitor();
		
		new RepositoryMining()
		.in(GitRepository.singleProject(path, true))
		.through(Commits.all())
		.process(visitor)
		.mine();
		
		Assert.assertEquals(3, visitor.getVisitedHashes().size());
		System.out.println(visitor.getVisitedHashes());
		Assert.assertTrue(visitor.getVisitedHashes().get(2).equals("ca4a534368fd04f8de76dcfbb1fa6a1b50a63887"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("80c6a1123d60a021c41d2581f8fbf6cfc2e38977"));
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("7a58b021f41b96a73d1383bb5e4e0feab1861327"));
	}
	
	
	
	@Test
	public void firstParentsOnlyInReverseOrder() {
		TestVisitor visitor = new TestVisitor();
		
		new RepositoryMining()
		.in(GitRepository.singleProject(path, true))
		.through(Commits.all())
		.reverseOrder()
		.process(visitor)
		.mine();
		
		Assert.assertEquals(3, visitor.getVisitedHashes().size());
		System.out.println(visitor.getVisitedHashes());
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("ca4a534368fd04f8de76dcfbb1fa6a1b50a63887"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("80c6a1123d60a021c41d2581f8fbf6cfc2e38977"));
		Assert.assertTrue(visitor.getVisitedHashes().get(2).equals("7a58b021f41b96a73d1383bb5e4e0feab1861327"));
	}
	
	
}
