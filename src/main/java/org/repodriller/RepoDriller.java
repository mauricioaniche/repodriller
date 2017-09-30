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

/**
 * This is the entry point for the RepoDriller framework.
 * Users should declare a RepoDriller and a Study, and then start() the Study.
 *
 * @author Mauricio Aniche
 */
public class RepoDriller {

	private static Logger log = Logger.getLogger(RepoDriller.class);

	public static void main(String[] args) {
		System.out.println("You should not run me! :/");
	}

	public RepoDriller() {
		log.info("# -------------------------------------------------- #");
		log.info("#                   RepoDriller                      #");
		log.info("#                     v1.3.1                         #");
		log.info("#               www.repodriller.org                  #");
		log.info("# -------------------------------------------------- #");
		log.info("");
	}

	/**
	 * {@link Study#execute} this study.
	 * Catches and logs any exceptions thrown.
	 * Has a stopwatch for you.
	 *
	 * @param study The study to be performed
	 */
	public void start(Study study) {
		/* TODO We could just make this static, no? What's the point in having a RepoDriller instance? */
		Calendar start = Calendar.getInstance();

		log.info("# -------------------------------------------------- #");
		log.info("Starting study at: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(start.getTime()));
		try {
			study.execute();
		} catch (Throwable t) {
			log.error("This study threw an exception:", t);
		}
		Calendar finish = Calendar.getInstance();
		log.info("Finished study at: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(finish.getTime()));

		long seconds = (finish.getTimeInMillis() - start.getTimeInMillis())/1000;
		long minutes = seconds/60;
		log.info("It took " + seconds + " seconds (~" + minutes + " minutes).");
		log.info("# -------------------------------------------------- #");
	}

}
