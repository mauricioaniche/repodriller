package org.repodriller.filter.range;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

/**
 * ChangeSets every X months.
 */
public class MonthlyCommits implements CommitRange {

	private final long monthsInMillis;
	private ChangeSet.Contributor whichContributorTime;
	private static final Logger log = LogManager.getLogger(MonthlyCommits.class);

	/**
	 * ChangeSets every {@code months} months.
	 * Uses AUTHOR time.
	 * This is probably not what you want in a complex git repo.
	 *
	 * @param months
	 */
	public MonthlyCommits(int months) {
		this(months, ChangeSet.Contributor.AUTHOR);
	}

	/**
	 * ChangeSets every {@code months} months.
	 * Uses the specified contributor time.
	 * You probably want to use this with CONTRIBUTOR time.
	 *
	 * @param months
	 * @param contributor
	 */
	public MonthlyCommits(int months, ChangeSet.Contributor contributor) {
		monthsInMillis = 1000L * 60L * 60L * 24L * 30L * months;
		whichContributorTime = contributor;
	}

	@Override
	public List<ChangeSet> get(SCM scm) {

		List<ChangeSet> all = scm.getChangeSets();

		LinkedList<ChangeSet> filtered = new LinkedList<ChangeSet>();
		filtered.add(all.get(0));

		/* all should be ordered with the most recently made ChangeSet first. */
		for(ChangeSet cs : all) {
			if(isFarFromTheLastOne(cs, filtered)) {
				filtered.addLast(cs);
			}
		}

		return filtered;
	}

	/**
	* True if {@code cs} was at least X months before the most recently accepted ChangeSet.
	*/
	private boolean isFarFromTheLastOne(ChangeSet cs, LinkedList<ChangeSet> filtered) {
		ChangeSet lastOne = filtered.getLast();

		long lastInMillis = lastOne.getContributor(whichContributorTime).time.getTimeInMillis();
		long currentInMillis = cs.getContributor(whichContributorTime).time.getTimeInMillis();

		return (lastInMillis - currentInMillis >= monthsInMillis);
	}

}
