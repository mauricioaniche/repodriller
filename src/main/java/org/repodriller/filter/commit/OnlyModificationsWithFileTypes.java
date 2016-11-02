package org.repodriller.filter.commit;

import java.util.List;

import org.repodriller.domain.Commit;

public class OnlyModificationsWithFileTypes implements CommitFilter{

	private List<String> fileExtensions;

	public OnlyModificationsWithFileTypes(List<String> fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	public boolean accept(Commit commit) {
		return commit.getModifications().stream().anyMatch(
				m -> fileExtensions.stream().anyMatch(fe -> m.fileNameEndsWith(fe)));
	}
	
}
