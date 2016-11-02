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

package org.repodriller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.filter.commit.CommitFilter;
import org.repodriller.filter.commit.NoFilter;
import org.repodriller.filter.range.CommitRange;
import org.repodriller.persistence.NoPersistence;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

import com.google.common.collect.Lists;

public class RepositoryMining {

	private List<SCMRepository> repos;
	private Map<CommitVisitor, PersistenceMechanism> visitors;
	
	private static Logger log = Logger.getLogger(RepositoryMining.class);
	private CommitRange range;
	private int threads;
	private boolean reverseOrder;
	private List<CommitFilter> filters;
	
	public RepositoryMining() {
		repos = new ArrayList<SCMRepository>();
		visitors = new HashMap<CommitVisitor, PersistenceMechanism>();
		filters = Arrays.asList((CommitFilter) new NoFilter());
		this.threads = 1;
	}
	
	public RepositoryMining through(CommitRange range) {
		this.range = range;
		return this;
	}
	
	public RepositoryMining in(SCMRepository... repo) {
		this.repos.addAll(Arrays.asList(repo));
		return this;
	}
	
	public RepositoryMining process(CommitVisitor visitor, PersistenceMechanism writer) {
		visitors.put(visitor, writer);
		return this;
	}
	
	public RepositoryMining filters(CommitFilter... filters) {
		this.filters = Arrays.asList(filters);
		return this;
	}
	
	public RepositoryMining reverseOrder() {
		reverseOrder = true;
		return this;
	}

	public RepositoryMining process(CommitVisitor visitor) {
		return process(visitor, new NoPersistence());
	}
	
	public void mine() {
		
		for(SCMRepository repo : repos) {
			processRepos(repo);
		}
		closeAllPersistence();
		printScript();
		
	}

	private void processRepos(SCMRepository repo) {
		log.info("Git repository in " + repo.getPath());
		
		List<ChangeSet> allCs = range.get(repo.getScm());
		if(!reverseOrder) Collections.reverse(allCs);
		
		log.info("Total of commits: " + allCs.size());
		
		log.info("Starting threads: " + threads);
		ExecutorService exec = Executors.newFixedThreadPool(threads);
		List<List<ChangeSet>> partitions = Lists.partition(allCs, threads);
		for(List<ChangeSet> partition : partitions) {
			
			exec.submit(() -> {
					for(ChangeSet cs : partition) {
						try {
							processChangeSet(repo, cs);
						} catch (OutOfMemoryError e) {
							System.err.println("Commit " + cs.getId() + " in " + repo.getLastDir() + " caused OOME");
							e.printStackTrace();
							System.err.println("goodbye :/");
							
							log.fatal("Commit " + cs.getId() + " in " + repo.getLastDir() + " caused OOME", e);
							log.fatal("Goodbye! ;/");
							System.exit(-1);
						} catch(Throwable t) {
							log.error(t);
						}
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

	private void printScript() {
		log.info("# --------------------------------------------------");
		log.info("Study has been executed in the following projects:");
		for(SCMRepository repo : repos) {
			log.info("- " + repo.getOrigin() + ", from " + repo.getFirstCommit() + " to " + repo.getHeadCommit());
		}
		
		log.info("The following processors were executed:");
		
		for(CommitVisitor visitor : visitors.keySet()) {
			log.info("- " + visitor.name() + " (" + visitor.getClass().getName() + ")");
		}
		
	}

	private void closeAllPersistence() {
		for(PersistenceMechanism persist : visitors.values()) {
			persist.close();
		}
	}
	
	private void processChangeSet(SCMRepository repo, ChangeSet cs) {
		Commit commit = repo.getScm().getCommit(cs.getId());
		log.info(
				"Commit #" + commit.getHash() + 
				" in " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(commit.getDate().getTime()) +
				" from " + commit.getAuthor().getName() + 
				" with " + commit.getModifications().size() + " modifications");

		if(!filtersAccept(commit)) {
			log.info("-> Filtered");
			return;
		}
		
		for(Map.Entry<CommitVisitor, PersistenceMechanism> entry : visitors.entrySet()) {
			CommitVisitor visitor = entry.getKey();
			PersistenceMechanism writer = entry.getValue();

			try {
				log.info("-> Processing " + commit.getHash() + " with " + visitor.name());
				visitor.process(repo, commit, writer);
			} catch (Exception e) {
				log.error("error processing #" + commit.getHash() + " in " + repo.getPath() + 
						", processor=" + visitor.name() + ", error=" + e.getMessage(), e);
			}
		}
		
	}

	private boolean filtersAccept(Commit commit) {
		for(CommitFilter filter : filters) {
			if(!filter.accept(commit)) return false;
		}
		return true;
	}

	public RepositoryMining withThreads(int n) {
		this.threads = n;
		return this;
	}
}
