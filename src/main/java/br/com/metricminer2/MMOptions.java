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

import java.util.Properties;

public class MMOptions {

	private Properties props;

	public MMOptions(Properties props) {
		this.props = props;
	}

	public String getCsv() {
		return props.getProperty("csv");
	}
	
	public String getStudy() {
		return props.getProperty("study");
	}

	public String getProjectsPath() {
		String projects = props.getProperty("projects");
		return projects == null ? "" : projects;
	}
	
	public boolean isStudy() {
		return props.containsKey("study");
	}

	public boolean hasCsv() {
		return props.containsKey("csv");
	}

	public int getThreads() {
		String threads = props.getProperty("threads");
		return threads == null ? 1 : Integer.valueOf(threads);
	}

	public String getScm() {
		return props.getProperty("scm");
	}

	public String getProjectPath() {
		String project = props.getProperty("project");
		return project == null ? "" : project;
	}
	
	public boolean isSysOut() {
		return props.containsKey("sysout");
	}

	public String specific(String project, String key) {
		return props.getProperty(project + "." + key);
	}
}
