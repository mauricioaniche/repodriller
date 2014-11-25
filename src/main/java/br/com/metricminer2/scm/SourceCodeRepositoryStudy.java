package br.com.metricminer2.scm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.metrics.MetricProcessor;

public class SourceCodeRepositoryStudy {

	private List<SCMRepository> repos;
	private Map<MetricProcessor, PersistenceMechanism> processors;
	
	private static Logger log = Logger.getLogger(SourceCodeRepositoryStudy.class);
	
	public SourceCodeRepositoryStudy() {
		repos = new ArrayList<SCMRepository>();
		processors = new HashMap<MetricProcessor, PersistenceMechanism>();
	}

	public SourceCodeRepositoryStudy in(SCMRepository... repo) {
		this.repos.addAll(Arrays.asList(repo));
		return this;
	}
	
	public SourceCodeRepositoryStudy process(MetricProcessor processor, PersistenceMechanism writer) {
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
		System.out.println("Study has been executed in the following projects:");
		System.out.println();
		for(SCMRepository repo : repos) {
			System.out.println("- " + repo.getOrigin() + ", from " + repo.getFirstCommit() + " to " + repo.getHeadCommit());
		}
		
		System.out.println();
		System.out.println("The following processors were executed:");
		System.out.println();
		
		for(MetricProcessor processor : processors.keySet()) {
			System.out.println("- " + processor.name() + "(" + processor.getClass().getName() + ")");
		}
		
		System.out.println();
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

		for(Map.Entry<MetricProcessor, PersistenceMechanism> entry : processors.entrySet()) {
			MetricProcessor processor = entry.getKey();
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
