package br.com.metricminer2.output.csv;

import java.io.PrintStream;

import br.com.metricminer2.persistence.PersistenceMechanism;

public class CSVWriter implements PersistenceMechanism {

	private PrintStream ps;

	public CSVWriter(String fileName) {
		try {
			ps = new PrintStream(fileName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void write(Object... line) {
		
		boolean first = true;
		for(Object o : line) {
			if(!first) ps.print(",");
			ps.print(o);
			first=false;
		}
		
		ps.println();
		ps.flush();

	}
	
	public void close() {
		ps.close();
	}

}
