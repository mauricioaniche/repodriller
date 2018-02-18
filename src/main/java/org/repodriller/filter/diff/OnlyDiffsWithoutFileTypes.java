package org.repodriller.filter.diff;

import java.util.List;

import org.repodriller.util.RDFileUtils;

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
		return !RDFileUtils.fileNameHasIsOfType(diffEntryPath, this.fileExtensions);
	}
}
