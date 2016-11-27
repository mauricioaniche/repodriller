package org.repodriller.scm;

import java.util.List;

import org.repodriller.domain.Commit;

public class CommitCounter {
	
	private final int totalCommits;
	private final List<String> hashes;
	
	public CommitCounter(List<String> hashes) {
		this.hashes = hashes;
		this.totalCommits = hashes.size();
	}
	
	public int getTotalCommits() {
		return totalCommits;
	}
	
	public CommitPosition count(Commit commit) {
		final int absolutePosition = totalCommits - hashes.indexOf(commit.getHash());
		final float relativePosition = absolutePosition * 100 / (float) totalCommits;
		return new CommitPosition(absolutePosition, relativePosition);
	}

}
