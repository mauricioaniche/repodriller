package br.com.metricminer2.study.selenium;

import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.Modification;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.processor.SCMProcessor;

public class CommittedTogether implements SCMProcessor {

	private boolean isCss(Modification m) {
		return m.getNewPath().toLowerCase().endsWith(".css") || m.getNewPath().toLowerCase().endsWith(".less");
	}

	private boolean isJava(Modification m) {
		return m.getNewPath().toLowerCase().endsWith(".java");
	}

	private boolean isHtml(Modification m) {
		return m.getNewPath().toLowerCase().endsWith(".html") || m.getNewPath().toLowerCase().endsWith(".jsp")
				|| m.getNewPath().toLowerCase().endsWith(".ftl") || m.getNewPath().toLowerCase().endsWith(".tag");
	}

	private boolean isJs(Modification m) {
		return m.getNewPath().toLowerCase().endsWith(".js");
	}

	private boolean isController(Modification m) {
		return m.getNewPath().toLowerCase().contains("controller");
	}

	private boolean hasASeleniumTest(Commit c) {

		for (Modification m : c.getModifications()) {
			if (Utils.isASeleniumTest(m))
				return true;
		}

		return false;
	}

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {

		if (hasASeleniumTest(commit)) {

			int qtyOfControllers = 0;
			int qtyOfCss = 0;
			int qtyOfJs = 0;
			int qtyOfHtml = 0;
			int qtyOfJava = 0;
			int qtyOfOthers = 0;

			for (Modification m : commit.getModifications()) {

				if (isController(m))
					qtyOfControllers++;
				else if (isCss(m))
					qtyOfCss++;
				else if (isJs(m))
					qtyOfJs++;
				else if (isHtml(m))
					qtyOfHtml++;
				else if (isJava(m))
					qtyOfJava++;
				else
					qtyOfOthers++;

			}

			writer.write(
				repo.getLastDir(),
				commit.getHash(),
				Utils.format(commit.getDate()),
				qtyOfControllers,
				qtyOfCss,
				qtyOfJs,
				qtyOfHtml,
				qtyOfJava,
				qtyOfOthers
			);
		}

	}

	@Override
	public String name() {
		return "committed together with selenium tests";
	}

}
