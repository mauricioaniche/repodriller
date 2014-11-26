package br.com.metricminer2;

import java.text.SimpleDateFormat;
import java.util.Date;

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
		
		System.out.println("# -------------------------------------------------- #");
		System.out.println("#                   MetricMiner                      #");
		System.out.println("#                      v2.0                          #");
		System.out.println("#             www.metricminer.org.br                 #");
		System.out.println("# -------------------------------------------------- #");
		System.out.println("Starting engine: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()));
		
		new MetricMiner2(args).start();
		
		System.out.println();
		System.out.println("Finished: " + new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(new Date()));
		System.out.println();
		System.out.println("Brought to you by MetricMiner (metricminer.org.br)");
		System.out.println("# -------------------------------------------------- #");
		
	}

	public void start() {
		try {
			Action.all().run(opts);
		} catch(Throwable ex) {
			ex.printStackTrace();
			jc.usage();
		}
	}


}
