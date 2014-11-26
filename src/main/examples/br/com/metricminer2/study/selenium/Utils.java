package br.com.metricminer2.study.selenium;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.metricminer2.domain.Modification;

public class Utils {

	public static String format(Calendar time) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time.getTime());
	}

	public static boolean isASeleniumTest(Modification m) {
		return m.getSourceCode().contains("selenium") && m.getNewPath().endsWith(".java") && !m.wasDeleted();
	}
}
