package br.com.metricminer2.executor;

import br.com.metricminer2.MMOptions;

public abstract class Executor {

	private Executor next = null;

	public Executor(Executor next) {
		this.next = next;
	}

	public final void run(MMOptions opts) {
		if (accept(opts))
			execute(opts);
		else if (next != null)
			next.run(opts);
	}

	protected abstract boolean accept(MMOptions opts);

	protected abstract void execute(MMOptions opts);

	public static Executor all() {
		return new StudyExecutor(new DetailExecutor());
	}

}
