package org.repodriller.filter.range;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

/**
 * All commits X such that from < X < to.
 */
public class BetweenDates implements CommitRange {

	private Calendar from;
	private Calendar to;
	private ChangeSet.Contributor whichContributorTime;

	/**
	 * ChangeSets such that from < X < to.
	 * Uses AUTHOR time.
	 * This is probably not what you want in a complex git repo.
	 *
	 * @param from
	 * @param to
	 */
	public BetweenDates(Calendar from, Calendar to) {
		this(from, to, ChangeSet.Contributor.AUTHOR);
	}

	/**
	 * ChangeSets such that from < X < to.
	 * Uses time associated with the specified contributor.
 	 * You probably want to use this with CONTRIBUTOR time.
	 *
	 * @param from	Optional, requires ChangeSets to occur after this date.
	 * @param to	Optional, requires ChangeSets to occur before this date.
	 * @param contributor	Which contributor's time to check?
	 */
	public BetweenDates(Calendar from, Calendar to, ChangeSet.Contributor contributor) {
		this.from = from;
		this.to = to;
		whichContributorTime = contributor;
	}

	@Override
	public List<ChangeSet> get(SCM scm) {

		List<ChangeSet> all = scm.getChangeSets();

		LinkedList<ChangeSet> filtered = new LinkedList<ChangeSet>();

		for(ChangeSet cs : all) {
			if(isInTheRange(cs)) {
				filtered.addLast(cs);
			}
		}

		return filtered;
	}

	private boolean isInTheRange(ChangeSet cs) {
		Calendar csTime = cs.getContributor(whichContributorTime).time;

		/* If a field is null, don't exclude any ChangeSets on that side of the inequality.
		 * Instead, reduce to a 1-sided or 0-sided inequality. */
		boolean afterFrom = true;
		if (from != null)
			afterFrom = csTime.after(from);

		boolean beforeTo = true;
		if (to != null)
			beforeTo = csTime.before(to);

		return afterFrom && beforeTo;
	}

}
