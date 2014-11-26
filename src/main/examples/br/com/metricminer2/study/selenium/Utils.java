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
