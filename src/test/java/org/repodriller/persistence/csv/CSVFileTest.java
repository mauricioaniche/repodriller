package org.repodriller.persistence.csv;

import org.junit.Before;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.filter.range.Commits;
import org.repodriller.scm.GitRepository;

public class CSVFileTest {


	private String pathToRepo7;

	@Before
	public void setUp() {
		this.pathToRepo7 = this.getClass().getResource("/").getPath() + "../../test-repos/git-7";
	}
	
	@Test(expected = CSVFileFormatException.class)
		public void createCSV() {
		TestCSVisitor visitor = new TestCSVisitor();
		CSVFile file = new CSVFile("C:/Users/dev/Desktop","test.csv", new String [] {"Hash"});
		
		new RepositoryMining()
			.in(GitRepository.singleProject(pathToRepo7))
			.through(Commits.all())
			.process(visitor, file)
			.mine();
		}
		
	}

