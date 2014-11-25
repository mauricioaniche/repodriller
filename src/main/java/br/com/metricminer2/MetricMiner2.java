package br.com.metricminer2;

import br.com.metricminer2.executor.Executor;

import com.beust.jcommander.JCommander;

public class MetricMiner2 {

	private MMOptions opts;
	private JCommander jc;

	public MetricMiner2(String[] args) {
		opts = new MMOptions();
		jc = new JCommander(opts, args);
	}
	
	public static void main(String[] args) {
		new MetricMiner2(args).start();
	}

	public void start() {
		try {
			Executor.all().run(opts);
		} catch(Throwable ex) {
			ex.printStackTrace();
			jc.usage();
		}
	}


}
