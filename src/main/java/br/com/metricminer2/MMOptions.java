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

import com.beust.jcommander.Parameter;


public class MMOptions {

	@Parameter(names="-study", description="Class with the study definition")
	private String study;
	
	@Parameter(names="-projects", description="Path to the projects that will be analyzed")
	private String projectsPath;

	@Parameter(names="-csv", description="Path to the CSV that will be outputed")
	private String csv;
	
	@Parameter(names="-detail", description="Give details from all projects")
	private boolean detail;
	
	@Parameter(names="-threads", description="Number of threads to use")
	private int threads;

	public String getCsv() {
		return csv;
	}
	
	public String getStudy() {
		return study;
	}

	public String getProjectsPath() {
		return projectsPath;
	}
	
	public boolean isDetail() {
		return detail;
	}
	
	public boolean isStudy() {
		return study!=null && !study.isEmpty();
	}

	public boolean hasCsv() {
		return csv!=null && !csv.isEmpty();
	}

	@Override
	public String toString() {
		return "MMOptions [study=" + study + ", projects=" + projectsPath + ", csv=" + csv + ", detail=" + detail + "]";
	}

	public int getThreads() {
		return threads == 0 ? 1 : threads;
	}
	
	
}
