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
import java.util.Calendar;

import org.apache.log4j.Logger;

public class RepoDriller {

	public static void main(String[] args) {
		System.out.println("You should not run me! :/");
	}
	
	private static Logger log = Logger.getLogger(RepoDriller.class);

	public void start(Study study) {

		try {
			
			Calendar startDate = Calendar.getInstance();
			
			log.info("# -------------------------------------------------- #");
			log.info("#                   RepoDriller                      #");
			log.info("#                     v1.1.0                         #");
			log.info("#               www.repodriller.org                  #");
			log.info("# -------------------------------------------------- #");
			log.info("Starting engine: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(startDate.getTime()));
			
			try {
				study.execute();
			} catch (Throwable t) {
				log.error("some study error came to me", t);
			}
			
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


}
