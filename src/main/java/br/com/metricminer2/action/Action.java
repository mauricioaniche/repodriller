/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
