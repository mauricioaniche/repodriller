package br.com.metricminer2.executor;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.SourceCodeRepositoryDetail;

public class DetailExecutor extends Executor {

	public DetailExecutor(Executor next) {
		super(next);
	}
	
	public DetailExecutor() {
		super(null);
	}

	@Override
	protected boolean accept(MMOptions opts) {
		return opts.isDetail();
	}

	@Override
	protected void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		SourceCodeRepositoryDetail detail = new SourceCodeRepositoryDetail(pm);
		detail.detail(opts.getProjects());
		pm.close();
	}

	
}
