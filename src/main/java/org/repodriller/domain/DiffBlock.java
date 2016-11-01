package org.repodriller.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.repodriller.RepoDrillerException;

public class DiffBlock {

	private int d1;
	private int d2;
	private int d3;
	private int d4;
	
	private String[] lines;
	private String diffBlock;

	public DiffBlock(String diffBlock) {
		this.diffBlock = diffBlock;
		this.lines = diffBlock.replace("\r", "").split("\n");
		
		getLinePositions();
	}
	
	private void getLinePositions() {
		String positions = lines[0];
		Pattern p = Pattern.compile("@@ -(\\d*),(\\d*) \\+(\\d*),(\\d*) @@.*");
		Matcher matcher = p.matcher(positions);
		
		if(matcher.matches()) {
			d1 = Integer.parseInt(matcher.group(1));
			d2 = Integer.parseInt(matcher.group(2));
			d3 = Integer.parseInt(matcher.group(3));
			d4 = Integer.parseInt(matcher.group(4));
		} else {
			throw new RepoDrillerException("Impossible to get line positions in this diff: " + diffBlock);
		}
	}

	public String[] getLines() {
		return lines;
	}

	private List<DiffLine> getLines(int start, int qtyLines, String ch) {
		List<DiffLine> oldLines = new ArrayList<>();
		int counter = start; 
		for(String line : lines) {
			if(line.startsWith(ch) || line.startsWith(" ")) {
				oldLines.add(new DiffLine(counter, line.substring(1)));
				counter++;
			}
		}
		if(counter!=start+qtyLines) throw new RepoDrillerException("malformed diff");
		
		return oldLines;
		
	}
	
	public List<DiffLine> getLinesInOldFile() {
		return getLines(d1, d2, "-");
	}

	public List<DiffLine> getLinesInNewFile() {
		return getLines(d3, d4, "+");
	}
	
	public String getDiffBlock() {
		return diffBlock;
	}
	
	
}
