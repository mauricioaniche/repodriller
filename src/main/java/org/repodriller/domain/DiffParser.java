package org.repodriller.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.repodriller.RepoDrillerException;

public class DiffParser {

	private String diff;
	private int d1;
	private int d2;
	private int d3;
	private int d4;

	String[] lines;
	
	public DiffParser(String diff) {
		this.diff = diff;
		this.lines = diff.replace("\r", "").split("\n");
		getLinePositions();
	}

	private List<DiffLine> getLines(int start, int qtyLines, String ch) {
		List<DiffLine> oldLines = new ArrayList<>();
		int counter = start; 
		for(String line : diffLines()) {
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
	
	private String[] diffLines() {
		return Arrays.copyOfRange(lines, 5, lines.length);
	}

	private void getLinePositions() {
		String positions = lines[4];
		Pattern p = Pattern.compile("@@ -(\\d*),(\\d*) \\+(\\d*),(\\d*) @@.*");
		Matcher matcher = p.matcher(positions);
		
		if(matcher.matches()) {
			d1 = Integer.parseInt(matcher.group(1));
			d2 = Integer.parseInt(matcher.group(2));
			d3 = Integer.parseInt(matcher.group(3));
			d4 = Integer.parseInt(matcher.group(4));
		} else {
			throw new RepoDrillerException("Impossible to get line positions in this diff: " + diff);
		}
	}


}
