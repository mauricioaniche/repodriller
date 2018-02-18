package org.repodriller.integration;

import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class ThrowingTest {

	private String path;

	@Before
	public void setUp() {
		this.path = this.getClass().getResource("/").getPath() + "../../test-repos/git-4";
	}

	@Test
	public void visitWhileVisitorThrows() {
		ThrowingTestVisitor visitor = new ThrowingTestVisitor();

		new RepositoryMining()
		.in(GitRepository.singleProject(path))
		.through(Commits.all())
		.process(visitor)
		.mine();

		// Hope nothing went wrong.
		return;
	}

}
