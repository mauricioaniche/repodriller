package br.com.metricminer2.action;

import br.com.metricminer2.MMOptions;

public abstract class Action {

	private Action next = null;

	public Action(Action next) {
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

	public static Action all() {
		return new StudyAction(new DetailAction());
	}

}
