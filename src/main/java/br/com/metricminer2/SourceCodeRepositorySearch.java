package br.com.metricminer2;

import java.util.List;

import br.com.metricminer2.output.PersistenceMechanism;
import br.com.metricminer2.scm.SCMProcessor;
import br.com.metricminer2.scm.SCMRepository;

public class SourceCodeRepositorySearch {

	public SourceCodeRepositorySearch in(List<SCMRepository> repos) {
		
		return this;
	}
	
	public SourceCodeRepositorySearch process(SCMProcessor processor, PersistenceMechanism writer) {
		
		return this;
	}
	
	public void start() {
		
	}
}
