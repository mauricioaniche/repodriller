package org.repodriller.integration;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.commit.OnlyModificationsWithFileTypes;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class OnlyModificationsWithFileTypeTest {


	private String pathToRepo7;

	@Before
	public void setUp() {
		this.pathToRepo7 = this.getClass().getResource("/").getPath() + "../../test-repos/git-7";
	}
	
	@Test
	public void shouldFilterCommits() {
		
		TestVisitor visitor = new TestVisitor();
		
		new RepositoryMining()
			.in(GitRepository.singleProject(pathToRepo7))
			.through(Commits.all())
			.filters(new OnlyModificationsWithFileTypes(Arrays.asList("java")))
			.process(visitor)
			.mine();
		
		Assert.assertEquals(2, visitor.getVisitedHashes().size());
		Assert.assertTrue(visitor.getVisitedHashes().get(0).equals("5adbb71167e79ab6b974827e74c9da4d81977655"));
		Assert.assertTrue(visitor.getVisitedHashes().get(1).equals("0577bec2387ee131e1ccf336adcc172224d3f6f9"));
	}
}
