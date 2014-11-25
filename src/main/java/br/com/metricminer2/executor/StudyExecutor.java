package br.com.metricminer2.executor;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.Study;

public class StudyExecutor extends Executor {

	public StudyExecutor(Executor next) {
		super(next);
	}
	
	public StudyExecutor() {
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
			throw new ExecutorException("error executing study", e);
		}

	}

}
