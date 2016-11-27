package org.repodriller.integration;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class RequiresCommitCounterTest {

	private String pathToRepo;

	@Before
	public void setUp() {
		this.pathToRepo = this.getClass().getResource("/").getPath() + "../../test-repos/git-3";
	}
	
	@Test
	public void shouldCountThroughAllCommits() {
		
		RequiresCommitCounterTestVisitor visitor = new RequiresCommitCounterTestVisitor();
		
		new RepositoryMining()
			.in(GitRepository.singleProject(pathToRepo))
			.through(Commits.all())
			.process(visitor)
			.mine();
		
		List<Integer> visitedAbsolutePositions = visitor.getVisitedAbsolutePositions();
		List<Float> visitedRelativePositions = visitor.getVisitedRelativePositions();
		Assert.assertEquals(16, visitedAbsolutePositions.size());
		for (Integer i = 1; i < 16; i++) {
			Assert.assertEquals(i, visitedAbsolutePositions.get(i-1));
			Assert.assertEquals((i/16f)*100, visitedRelativePositions.get(i-1), 0.01d);
		}
	}
	
	@Test
	public void shouldCountHeadOnly() {
		
		RequiresCommitCounterTestVisitor visitor = new RequiresCommitCounterTestVisitor();
		
		new RepositoryMining()
			.in(GitRepository.singleProject(pathToRepo))
			.through(Commits.onlyInHead())
			.process(visitor)
			.mine();
		
		Assert.assertEquals(1, visitor.getVisitedAbsolutePositions().size());
		Assert.assertEquals(16, visitor.getVisitedAbsolutePositions().get(0), 0);
		Assert.assertEquals(100d, visitor.getVisitedRelativePositions().get(0), 0d);
	}
	
	@Test
	public void shouldCountFirstCommit() {
		
		RequiresCommitCounterTestVisitor visitor = new RequiresCommitCounterTestVisitor();
		
		String firstHash = "866e997a9e44cb4ddd9e00efe49361420aff2559";
		new RepositoryMining()
			.in(GitRepository.singleProject(pathToRepo))
			.through(Commits.single(firstHash))
			.process(visitor)
			.mine();
		
		Assert.assertEquals(1, visitor.getVisitedAbsolutePositions().size());
		Assert.assertEquals(firstHash, visitor.getVisitedHashes().get(0));
		Assert.assertEquals(1, visitor.getVisitedAbsolutePositions().get(0), 0);
		Assert.assertEquals(6.25d, visitor.getVisitedRelativePositions().get(0), 0d);
	}
	
}
