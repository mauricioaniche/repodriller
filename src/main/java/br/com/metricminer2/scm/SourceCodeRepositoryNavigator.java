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

package br.com.metricminer2.scm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import br.com.metricminer2.MMOptions;
import br.com.metricminer2.domain.ChangeSet;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.persistence.PersistenceMechanism;

import com.google.common.collect.Lists;

public class SourceCodeRepositoryNavigator {

	private List<SCMRepository> repos;
	private Map<CommitVisitor, PersistenceMechanism> visitors;
	
	private static Logger log = Logger.getLogger(SourceCodeRepositoryNavigator.class);
	private MMOptions opts;
	
	public SourceCodeRepositoryNavigator(MMOptions opts) {
		this.opts = opts;
		repos = new ArrayList<SCMRepository>();
		visitors = new HashMap<CommitVisitor, PersistenceMechanism>();
	}
	
	public SourceCodeRepositoryNavigator scmFromConfig() {
		
		if(opts.getScm().equals("git")) {
			if(!opts.getProjectPath().isEmpty()) {
				in(GitRepository.build(opts.getProjectPath()));
			} else if(!opts.getProjectsPath().isEmpty()) {
				in(GitRepository.allIn(opts.getProjectsPath()));
			}
		}
		return this;
	}

	public SourceCodeRepositoryNavigator in(SCMRepository... repo) {
		this.repos.addAll(Arrays.asList(repo));
		return this;
	}
	
	public SourceCodeRepositoryNavigator process(CommitVisitor visitor, PersistenceMechanism writer) {
		visitors.put(visitor, writer);
		return this;
	}
	
	public void start() {
		for(SCMRepository repo : repos) {
			log.info("Git repository in " + repo.getPath());
			
			List<ChangeSet> allCs = repo.getScm().getChangeSets();
			log.info("Total of commits: " + allCs.size());
			
			log.info("Starting " + opts.getThreads() + " threads");
			ExecutorService exec = Executors.newFixedThreadPool(opts.getThreads());
			List<List<ChangeSet>> partitions = Lists.partition(allCs, opts.getThreads());
			for(List<ChangeSet> partition : partitions) {
				
				exec.submit(() -> {
					for(ChangeSet cs : partition) {
						processEverythingOnChangeSet(repo, cs);
					}
				});
			}
			
			try {
				exec.shutdown();
				exec.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				log.error("error waiting for threads to terminate in " + repo.getLastDir(), e);
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
		
		for(CommitVisitor visitor : visitors.keySet()) {
			System.out.println("- " + visitor.name() + " (" + visitor.getClass().getName() + ")");
		}
		
		System.out.println();
	}

	private void closeAllPersistence() {
		for(PersistenceMechanism persist : visitors.values()) {
			persist.close();
		}
	}

	private void processEverythingOnChangeSet(SCMRepository repo, ChangeSet cs) {
		Commit commit = repo.getScm().getCommit(cs.getId());
		log.info(
				"Commit #" + commit.getHash() + 
				" in " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(commit.getDate().getTime()) +
				" from " + commit.getCommitter().getName() + 
				" with " + commit.getModifications().size() + " modifications");

		for(Map.Entry<CommitVisitor, PersistenceMechanism> entry : visitors.entrySet()) {
			CommitVisitor visitor = entry.getKey();
			PersistenceMechanism writer = entry.getValue();

			try {
				log.info("-> Processing " + commit.getHash() + " with " + visitor.name());
				visitor.process(repo, commit, writer);
			} catch (Exception e) {
				log.error("error processing #" + commit.getHash() + " in " + repo.getPath() + 
						", processor=" + visitor.name(), e);
			}
		}
	}
}
