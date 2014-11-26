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

import br.com.metricminer2.action.Action;

import com.beust.jcommander.JCommander;

public class MetricMiner2 {

	private MMOptions opts;
	private JCommander jc;

	public MetricMiner2(String[] args) {
		opts = new MMOptions();
		jc = new JCommander(opts, args);
	}
	
	public static void main(String[] args) {
		
//		args = new String[] {"-scm", "git", "-study", "br.com.metricminer2.examples.Example3", "-project", "/Users/mauricioaniche/workspace/metricminer2", "-csv", "/Users/mauricioaniche/Desktop/", "-threads", "1"};
		
		new MetricMiner2(args).start();
		
	}

	public void start() {
		if(opts.isUsage()) {
			jc.usage();
			System.exit(0);
		}
		
		try {
			
			Calendar startDate = Calendar.getInstance();
			
			System.out.println("# -------------------------------------------------- #");
			System.out.println("#                   MetricMiner                      #");
			System.out.println("#                      v2.0                          #");
			System.out.println("#             www.metricminer.org.br                 #");
			System.out.println("# -------------------------------------------------- #");
			System.out.println("Starting engine: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(startDate.getTime()));
			
			Action.all().run(opts);
			
			Calendar finishDate = Calendar.getInstance();
			System.out.println();
			System.out.println("Finished: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(finishDate.getTime()));
			System.out.println("It took " + (finishDate.getTimeInMillis() - startDate.getTimeInMillis())/1000 + " seconds.");
			System.out.println();
			System.out.println("Brought to you by MetricMiner (metricminer.org.br)");
			System.out.println("# -------------------------------------------------- #");
			
		} catch(Throwable ex) {
			jc.usage();
			ex.printStackTrace();
		}
	}


}
