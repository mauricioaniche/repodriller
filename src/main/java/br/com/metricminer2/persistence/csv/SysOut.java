package br.com.metricminer2.persistence.csv;

import br.com.metricminer2.persistence.PersistenceMechanism;

public class SysOut implements PersistenceMechanism {

	@Override
	public void write(Object... line) {
		boolean first = true;
		for(Object o : line) {
			if(!first) System.out.print(",");
			System.out.print(o);
			first=false;
		}

		System.out.println();
	}

	@Override
	public void close() {
		
	}

}
