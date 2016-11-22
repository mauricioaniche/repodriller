package org.repodriller.persistence.csv;

import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

public class TestCSVisitor implements CommitVisitor {	
	
	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		
		writer.write(
				commit.getAuthor().getName(),
				commit.getHash()		
				
				);
	}

	@Override
	public String name() {
		return "test";
	}

}

