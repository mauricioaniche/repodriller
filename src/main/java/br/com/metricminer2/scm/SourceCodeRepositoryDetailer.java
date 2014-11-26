/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
