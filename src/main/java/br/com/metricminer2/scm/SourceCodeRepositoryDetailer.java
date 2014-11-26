package br.com.metricminer2.scm;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Modification;
import br.com.metricminer2.persistence.PersistenceMechanism;

public class SourceCodeRepositoryDetailer {

	
	private PersistenceMechanism ps;

	public SourceCodeRepositoryDetailer(PersistenceMechanism ps) {
		this.ps = ps;
	}
	
	public void detail(String projectsDir) {
		SCMRepository[] repos = GitRepository.allIn(projectsDir);
		
		for(SCMRepository repo : repos) {
			
			Commit lastCommit = repo.getScm().getCommit(repo.getHeadCommit());
			int qtyOfCommits = countCommits(repo);
			int qtyOfJavaFilesInLastCommit = count(lastCommit, "java", true);
			int qtyOfTotalFiles = count(lastCommit, "java", false);
			
			ps.write(
				repo.getLastDir(), 
				repo.getOrigin(), 
				repo.getFirstCommit(), 
				repo.getHeadCommit(),
				qtyOfCommits,
				qtyOfJavaFilesInLastCommit,
				qtyOfTotalFiles);
		}
	}

	private int count(Commit c, String suffix, boolean with) {
		
		int count = 0;
		for(Modification m : c.getModifications()) {
			if(with && m.getNewPath().endsWith(suffix)) count++;
			else if(!with && !m.getNewPath().endsWith(suffix)) count++;
		}
		
		return count;
	}

	private int countCommits(SCMRepository repo) {
		return repo.getScm().getChangeSets().size();
	}
}
