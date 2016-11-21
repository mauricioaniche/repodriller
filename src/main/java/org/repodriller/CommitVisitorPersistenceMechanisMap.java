package org.repodriller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

public class CommitVisitorPersistenceMechanisMap {

	private Map<CommitVisitor, PersistenceMechanism> visitors;
	private Logger log;
	
	public CommitVisitorPersistenceMechanisMap(Logger log) {
		this.log = log;
		visitors = new HashMap<CommitVisitor, PersistenceMechanism>();
	}

	void initializeVisitors(SCMRepository repo) {
		visitors.forEach((visitor,writer) -> {
			try {
				log.info("-> Initializing visitor " + visitor.name());
				visitor.initialize(repo, writer);
			} catch (Exception e) {
				log.error("error in " + repo.getPath() + 
						"when initializing " + visitor.name() + ", error=" + e.getMessage(), e);
			}
		});
	}

	void processCommit(SCMRepository repo, Commit commit) {
		visitors.forEach((visitor,writer) -> {
			try {
				log.info("-> Processing " + commit.getHash() + " with " + visitor.name());
				visitor.process(repo, commit, writer);
			} catch (Exception e) {
				log.error("error processing #" + commit.getHash() + " in " + repo.getPath() + 
						", processor=" + visitor.name() + ", error=" + e.getMessage(), e);
			}
		});
	}
	
	void finalizeVisitors(SCMRepository repo) {
		visitors.forEach((visitor,writer) -> {
			try {
				log.info("-> Finalizing visitor " + visitor.name());
				visitor.finalize(repo, writer);
			} catch (Exception e) {
				log.error("error in " + repo.getPath() + 
						"when finalizing " + visitor.name() + ", error=" + e.getMessage(), e);
			}
		});
	}
	
	void closeAllPersistence() {
		for(PersistenceMechanism persist : visitors.values()) {
			persist.close();
		}
	}

	void printScript() {
		for(CommitVisitor visitor : visitors.keySet()) {
			log.info("- " + visitor.name() + " (" + visitor.getClass().getName() + ")");
		}
	}

	public void put(CommitVisitor visitor, PersistenceMechanism writer) {
		this.visitors.put(visitor, writer);
	}
	
}
