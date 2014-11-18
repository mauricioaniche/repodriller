package br.com.metricminer2.examples;

import br.com.metricminer2.SourceCodeRepositorySearch;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.git.GitRepository;
import br.com.metricminer2.scm.processor.SCMProcessor;

public class Example4 {


	public static void main(String[] args) {
		String repoPath = Example1.class.getResource("/repo-1/").getPath();
		String out = Example1.class.getResource("/").getPath() + "msgs.csv";
		
		new SourceCodeRepositorySearch()
			.in(GitRepository.build(repoPath))
			.process(new SCMProcessor() {
				
				@Override
				public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
					writer.write(commit.getHash(), commit.getMsg());
				}
				
				@Override
				public String name() {
					return "commit messages";
				}
			}, new CSVFile(out))
			.start();
	}
}
