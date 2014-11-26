package br.com.metricminer2.action;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.PersistenceMechanismBuilder;
import br.com.metricminer2.scm.SourceCodeRepositoryDetailer;

public class DetailAction extends Action {

	public DetailAction(Action next) {
		super(next);
	}
	
	public DetailAction() {
		super(null);
	}

	@Override
	protected boolean accept(MMOptions opts) {
		return opts.isDetail();
	}

	@Override
	protected void execute(MMOptions opts) {
		PersistenceMechanism pm = new PersistenceMechanismBuilder().from(opts);
		SourceCodeRepositoryDetailer detail = new SourceCodeRepositoryDetailer(pm);
		detail.detail(opts.getProjectsPath());
		pm.close();
	}

	
}
