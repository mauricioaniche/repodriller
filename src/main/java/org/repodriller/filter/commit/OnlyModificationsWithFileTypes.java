package org.repodriller.filter.commit;

import java.util.List;

import org.repodriller.domain.Commit;

public class OnlyModificationsWithFileTypes extends CommitFilter{


	private List<String> fileExtensions;

	public OnlyModificationsWithFileTypes(List<String> fileExtensions) {
		this(fileExtensions, null);
	}

	public OnlyModificationsWithFileTypes(List<String> fileExtensions, CommitFilter filter) {
		super(filter);
		this.fileExtensions = fileExtensions;
	}

	@Override
	protected boolean shouldAccept(Commit commit) {
		return commit.getModifications().stream().anyMatch(
				m -> fileExtensions.stream().anyMatch(fe -> m.fileNameEndsWith(fe)));
	}
	
}
