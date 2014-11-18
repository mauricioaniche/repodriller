package br.com.metricminer2.persistence.csv;

import java.io.PrintStream;

import br.com.metricminer2.persistence.PersistenceMechanism;

public class CSVFile implements PersistenceMechanism {

	private PrintStream ps;

	public CSVFile(String fileName) {
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
