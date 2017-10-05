package org.repodriller.filter.range;

import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

/**
 * Return a set of commits from an SCM.
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
