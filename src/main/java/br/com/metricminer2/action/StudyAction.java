package br.com.metricminer2.action;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;

public class StudyAction extends Action {

	public StudyAction(Action next) {
		super(next);
	}
	
	public StudyAction() {
		super(null);
	}
	
	@Override
	protected boolean accept(MMOptions opts) {
		return opts.isStudy();
	}

	@Override
	protected void execute(MMOptions opts) {
		try {
			Class<?> studyClazz = Class.forName(opts.getStudy());
			Study study = (Study) studyClazz.newInstance();
			study.execute(opts);

		} catch (Exception e) {
			throw new ActionException("error executing study", e);
		}

	}

}
