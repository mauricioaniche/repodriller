package org.repodriller.filter.range;

import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

/**
 * A CommitRange is a subset of all commits from the SCM, returned in the order in which they should be processed.
 *
 * @author Mauricio Aniche
 */
/* TODO It's confusing that this interface is called CommitRange but it returns a list of ChangeSet's, not Commits. */
public interface CommitRange {

	/**
	 * Extract the desired commits from this SCM.
	 *
	 * @param scm	The SCM to probe.
	 * @return	List of the ChangeSet's in the range.
	 */
	List<ChangeSet> get(SCM scm);

}
