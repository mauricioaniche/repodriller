package br.com.metricminer2.integration;

import java.util.ArrayList;
import java.util.List;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.CommitVisitor;
import br.com.metricminer2.scm.SCMRepository;

public class TestVisitor implements CommitVisitor {

	private List<String> visitedCommits;
	
	public TestVisitor() {
		visitedCommits = new ArrayList<>();
	}
	
	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		visitedCommits.add(commit.getHash());
	}

	@Override
	public String name() {
		return "test";
	}

	public List<String> getVisitedCommits() {
		return visitedCommits;
	}

}
