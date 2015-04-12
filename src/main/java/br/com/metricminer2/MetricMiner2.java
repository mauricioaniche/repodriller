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

package br.com.metricminer2;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class MetricMiner2 {

	private MMOptions opts;
	private static Logger log = Logger.getLogger(MetricMiner2.class);

	public MetricMiner2() {
		opts = new MMOptionsReader().read();
	}
	
	public static void main(String[] args) {
		
//		args = new String[] {"-scm", "git", "-study", "br.com.metricminer2.examples.Example3", "-project", "/Users/mauricioaniche/workspace/metricminer2", "-csv", "/Users/mauricioaniche/Desktop/", "-threads", "1"};
		
		new MetricMiner2().start();
		
	}

	public void start() {

		try {
			
			Calendar startDate = Calendar.getInstance();
			
			log.info("# -------------------------------------------------- #");
			log.info("#                   MetricMiner                      #");
			log.info("#                      v2.0                          #");
			log.info("#             www.metricminer.org.br                 #");
			log.info("# -------------------------------------------------- #");
			log.info("Starting engine: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(startDate.getTime()));
			
			executeStudy();
			
			Calendar finishDate = Calendar.getInstance();
			log.info("Finished: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(finishDate.getTime()));
			long seconds = (finishDate.getTimeInMillis() - startDate.getTimeInMillis())/1000;
			log.info("It took " + seconds + " seconds (~" + seconds/60 + " minutes).");
			log.info("Brought to you by MetricMiner2 (metricminer.org.br)");
			log.info("# -------------------------------------------------- #");
			
		} catch(Throwable ex) {
			log.error("Some error ocurred", ex);
		}
	}

	private void executeStudy() throws Exception {
		log.info("Initializing study in " + opts.getStudy());
		Class<?> studyClazz = Class.forName(opts.getStudy());
		Study study = (Study) studyClazz.newInstance();
		study.execute(opts);
	}


}
