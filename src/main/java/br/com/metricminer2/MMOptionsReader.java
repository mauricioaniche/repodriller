package br.com.metricminer2;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class MMOptionsReader {
	
	private static Logger log = Logger.getLogger(MMOptionsReader.class);

	public MMOptions read() {

		try {

			FileInputStream is = new FileInputStream(new File("research.mm2"));
			Properties props = new Properties();
			props.load(is);

			MMOptions opts = new MMOptions(props);
			
			is.close();
			return opts;
		} catch (Exception e) {
			log.error("Where is the 'research.mm2'? Are you kidding me, kiddo!?");
			System.exit(1);
			return null;
		}
	}

}
