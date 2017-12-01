package org.repodriller.filter.diff;

import java.util.List;

/**
 * Only process diffs on files without certain file extensions.
 * 
 * @author Ayaan Kazerouni
 *
 */
public class OnlyDiffsWithoutFileTypes implements DiffFilter {
	
	private List<String> fileExtensions;
	
	public OnlyDiffsWithoutFileTypes(List<String> fileExtensions) {
		this.fileExtensions = fileExtensions;
	}

	@Override
	public boolean accept(String diffEntryPath) {
		return this.fileExtensions.stream().noneMatch(fe -> fe.endsWith(diffEntryPath));
	}
}
