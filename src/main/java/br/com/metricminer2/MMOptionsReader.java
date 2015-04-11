package br.com.metricminer2;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MMOptionsReader {
	
	private static Logger log = Logger.getLogger(MMOptionsReader.class);

	public MMOptions read() {

		try {
			MMOptions opts = new MMOptions();

			FileInputStream is = new FileInputStream(new File("research.mm2"));
			Properties props = new Properties();
			props.load(is);
			
			if(props.containsKey("study"))
				opts.setStudy(props.getProperty("study"));
			if(props.containsKey("projects"))
				opts.setProjectsPath(props.getProperty("projects"));
			if(props.containsKey("project"))
				opts.setProjectPath(props.getProperty("project"));
			if(props.containsKey("csv"))
				opts.setCsv(props.getProperty("csv"));
			if(props.containsKey("scm"))
				opts.setScm(props.getProperty("scm"));
			if(props.containsKey("threads"))
				opts.setThreads(Integer.valueOf(props.getProperty("threads")));
			if(props.containsKey("sysOut"))
				opts.setSysOut(true);

			is.close();
			return opts;
		} catch (Exception e) {
			log.error("Where is the 'research.mm2'? Are you kidding me, kiddo!?");
			System.exit(1);
			return null;
		}
	}

}
