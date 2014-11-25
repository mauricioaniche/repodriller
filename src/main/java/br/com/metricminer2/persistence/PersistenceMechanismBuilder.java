package br.com.metricminer2.persistence;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.persistence.csv.CSVFile;

public class PersistenceMechanismBuilder {

	public PersistenceMechanism from(MMOptions opts) {
		if(opts.hasCsv()) {
			return new CSVFile(opts.getCsv());
		}

		throw new PersistenceMechanismException("Persistence mechanism not found.");
	}
}
