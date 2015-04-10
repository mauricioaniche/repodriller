package br.com.metricminer2;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.beust.jcommander.IDefaultProvider;

public class PropertiesProvider implements IDefaultProvider {

	private Properties props;
	private static Logger log = Logger.getLogger(PropertiesProvider.class);

	public PropertiesProvider() {
		try {
			FileInputStream is = new FileInputStream(new File("."));
			props = new Properties();
			props.load(is);
		} catch (Exception e) {
			log.error("No args and no 'research.mm2' file? Are you here to play with me, kiddo!?");
			System.exit(1);
		}
	}
	
	@Override
	public String getDefaultValueFor(String optionName) {
		System.out.println(optionName);
		String realOptionName = optionName.substring(1);
		if(props.contains(realOptionName)) {
			return props.getProperty(realOptionName);
		}
		
		return null;
	}

}
