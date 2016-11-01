package org.repodriller.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiffParser {

	private List<DiffBlock> diffBlocks;

	private String fullDiff;
	
	public DiffParser(String fullDiff) {
		this.fullDiff = fullDiff;
		diffBlocks = new ArrayList<>();
		
		extractDiffBlocks();
	}

	private void extractDiffBlocks() {
		String[] lines = fullDiff.replace("\r", "").split("\n");
		String[] linesNoHeader = Arrays.copyOfRange(lines, 4, lines.length);

		StringBuilder currentDiff = new StringBuilder();
		boolean currentInADiff = false;
		
		for(int i = 0; i < linesNoHeader.length; i++) {
			String currentLine = linesNoHeader[i];
			if(currentLine.startsWith("@@ -") && !currentInADiff) {
				currentInADiff = true;
			}
			else if(currentLine.startsWith("@@ -") && currentInADiff) {
				diffBlocks.add(new DiffBlock(currentDiff.toString()));
				currentDiff = new StringBuilder();
				currentInADiff = false;
				i--;
			}

			if(currentInADiff) currentDiff.append(currentLine + "\n");
		}
		diffBlocks.add(new DiffBlock(currentDiff.toString()));

	}
	
	public List<DiffBlock> getBlocks() {
		return diffBlocks;
	}

	public String getFullDiff() {
		return fullDiff;
	}

}
