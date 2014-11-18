package br.com.metricminer2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.ChangeSet;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.processor.SCMProcessor;

public class SourceCodeRepositorySearch {

	private List<SCMRepository> repos;
	private Map<SCMProcessor, PersistenceMechanism> processors;
	
	private static Logger log = Logger.getLogger(SourceCodeRepositorySearch.class);
	
	public SourceCodeRepositorySearch() {
		repos = new ArrayList<SCMRepository>();
		processors = new HashMap<SCMProcessor, PersistenceMechanism>();
	}

	public SourceCodeRepositorySearch in(SCMRepository... repo) {
		this.repos.addAll(Arrays.asList(repo));
		return this;
	}
	
	public SourceCodeRepositorySearch process(SCMProcessor processor, PersistenceMechanism writer) {
		processors.put(processor, writer);
		return this;
	}
	
	public void start() {
		for(SCMRepository repo : repos) {
			log.info("Git repository in " + repo.getPath());
			
			List<ChangeSet> allCs = repo.getScm().getChangeSets();
			log.info("Total of commits: " + allCs.size());
			for(ChangeSet cs : allCs) {
				processEverythingOnChangeSet(repo, cs);
			}
		}
		
		closeAllPersistence();
		printScript();
		
	}

	private void printScript() {
		System.out.println("# --------------------------------------------------");
		System.out.println("MetricMiner2 was executed in the following projects:");
		System.out.println();
		for(SCMRepository repo : repos) {
			System.out.println("- " + repo.getOrigin() + ", from " + repo.getLastCommit() + " to " + repo.getHeadCommit());
		}
		
		System.out.println();
		System.out.println("The following processors were executed:");
		System.out.println();
		
		for(SCMProcessor processor : processors.keySet()) {
			System.out.println("- " + processor.name());
		}
		
		System.out.println();
		System.out.println("This execution happened on " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()) + ".");
		System.out.println();
		System.out.println("Brought to you by MetricMiner (metricminer.org.br)");
		System.out.println("# --------------------------------------------------");
	}

	private void closeAllPersistence() {
		for(PersistenceMechanism persist : processors.values()) {
			persist.close();
		}
	}

	private void processEverythingOnChangeSet(SCMRepository repo, ChangeSet cs) {
		Commit commit = repo.getScm().detail(cs.getId());
		log.info(
				"Commit #" + commit.getHash() + 
				" in " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(commit.getDate().getTime()) +
				" from " + commit.getCommitter().getName() + 
				" with " + commit.getModifications().size() + " modifications");

		for(Map.Entry<SCMProcessor, PersistenceMechanism> entry : processors.entrySet()) {
			SCMProcessor processor = entry.getKey();
			PersistenceMechanism writer = entry.getValue();

			try {
				log.info("-> Processing " + commit.getHash() + " with " + processor.name());
				processor.process(repo, commit, writer);
			} catch (Exception e) {
				log.error("error processing #" + commit.getHash() + " in " + repo.getPath() + 
						", processor=" + processor.name(), e);
			}
		}
	}
}
