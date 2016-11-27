package org.repodriller.scm;

import java.util.List;
import java.util.stream.Collectors;

import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.persistence.PersistenceMechanism;

public interface RequiresCommitCounter {
	
	default void initialize(SCMRepository repo, PersistenceMechanism writer) {
		List<ChangeSet> changeSets = repo.getScm().getChangeSets();
		List<String> hashes = changeSets.stream().map((cs)->cs.getId()).collect(Collectors.toList());
		this.setCommitCounter(new CommitCounter(hashes));
	}
	
	void setCommitCounter(CommitCounter commitCounter);
	void count(Commit commit);

}
