package org.repodriller.filter.range;

import java.util.Calendar;
import java.util.List;

/**
 * Factory for different kinds of CommitRange's.
 *
 * @author Mauricio Aniche
 *
 */
public class Commits {

	/**
	 * @return	A CommitRange that returns all commits.
	 */
	public static CommitRange all() {
		return new AllCommits();
	}

	/**
	 * @return	A CommitRange that returns all commits in Head.
	 */
	public static CommitRange onlyInHead() {
		return new OnlyInHead();
	}

	/**
	 * @param commitId	The commit Id
	 * @return 			A CommitRange that returns the commit with ID {@code commitId}
	 */
	public static CommitRange single(String commitId) {
		return new SingleCommit(commitId);
	}

	/**
	 * @param date	Earliest date of commits to return
	 * @return		A CommitRange that returns commits since {@code date}
	 */
	public static CommitRange since(Calendar date) {
		return new SinceCommit(date);
	}

	/**
	 * @param months		Number of months to skip between commits
	 * @return			A CommitRange that returns the first commit every {@code months} apart
	 */
	public static CommitRange monthly(int months) {
		return new MonthlyCommits(months);
	}

	/**
	 * @param days		Number of days to skip between commits
	 * @return			A CommitRange that returns the first commit every {@code months} apart
	 */
	public static CommitRange daily(int days) {
		return new DailyCommits(days);
	}

	/**
	 * @param from	Earliest commit was made at or after {@code from}
	 * @param to	Latest commit was before or at {@code to}
	 * @return		A CommitRange that returns commits between {@code from} and {@code to}
	 */
	public static CommitRange betweenDates(Calendar from, Calendar to) {
		return new BetweenDates(from, to);
	}

	/**
	 * @param commits	The superset of possible commits to include
	 * @return			A CommitRange that returns all of the commits from {@code commits} present in the SCM
	 */
	public static CommitRange list(List<String> commits) {
		return new ListOfCommits(commits);
	}

	/**
	 * @param start	First commit to include
	 * @param end	Last commit to include
	 * @return		A CommitRange that returns all commits between {@code start} and {@code end}, inclusive.
	 * 				If {@code start} is not found, returns nothing.
	 * 				If only {@code start} is found, returns all commits from there on.
	 */
	public static CommitRange range(String start, String end) {
		return new Range(start, end);
	}
	
	public static CommitRange betweenTags(String from, String to){
		return new BetweenTags(from, to);
	}

	public static CommitRange numOfCommits(int numOfCommits){
		return new NumOfCommits(numOfCommits);
	}
}
