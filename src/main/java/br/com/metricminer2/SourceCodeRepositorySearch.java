package br.com.metricminer2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.com.metricminer2.output.PersistenceMechanism;
import br.com.metricminer2.scm.ChangeSet;
import br.com.metricminer2.scm.Commit;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.git.GitRepository;
import br.com.metricminer2.scm.processor.SCMProcessor;

public class SourceCodeRepositorySearch {

	private List<String> reposPath;
	private Map<SCMProcessor, PersistenceMechanism> processors;
	private GitRepository git;
	
	private static Logger log = Logger.getLogger(SourceCodeRepositorySearch.class);
	
	public SourceCodeRepositorySearch() {
		reposPath = new ArrayList<String>();
		processors = new HashMap<SCMProcessor, PersistenceMechanism>();
		git = new GitRepository();
	}

	public SourceCodeRepositorySearch in(List<String> reposPath) {
		this.reposPath = reposPath;
		return this;
	}
	
	public SourceCodeRepositorySearch process(SCMProcessor processor, PersistenceMechanism writer) {
		processors.put(processor, writer);
		return this;
	}
	
	public void start() {
		for(String path : reposPath) {
			SCMRepository repoInfo = git.info(path);
			log.info("Git repository in " + path);
			
			List<ChangeSet> allCs = git.getChangeSets(repoInfo.getPath());
			log.info("Total of commits: " + allCs.size());
			for(ChangeSet cs : allCs) {
				processEverythingOnChangeSet(repoInfo, cs);
			}
		}
		
		closeAllPersistence();
		
	}

	private void closeAllPersistence() {
		for(PersistenceMechanism persist : processors.values()) {
			persist.close();
		}
	}

	private void processEverythingOnChangeSet(SCMRepository repoInfo, ChangeSet cs) {
		Commit commit = git.detail(cs.getId(), repoInfo.getPath());
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
				processor.process(repoInfo, commit, writer);
			} catch (Exception e) {
				log.error("error processing #" + commit.getHash() + " in " + repoInfo.getPath() + 
						", processor=" + processor.name(), e);
			}
		}
	}
}
