package org.repodriller.filter.commit;

import org.repodriller.domain.Commit;

public abstract class CommitFilter {

	private CommitFilter next;
	
	public CommitFilter(CommitFilter next) {
		this.next = next;
	}
	
	public CommitFilter() {
		this.next = null;
	}
	
	public final boolean accept(Commit commit) {
		boolean shouldAccept = shouldAccept(commit);
		if(!shouldAccept) return false;
		
		if(next!=null) return next.accept(commit);
		return true;
	}
	
	protected abstract boolean shouldAccept(Commit commit);
	
	
}
