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

	@Parameter(names="-project", description="Path to a single project that will be analyzed")
	private String projectPath;

	@Parameter(names="-csv", description="Path to the CSV that will be outputed")
	private String csv;

	@Parameter(names="-scm", description="SCM to be used (git)")
	private String scm;
	
	@Parameter(names="-detail", description="Give details from all projects")
	private boolean detail;

	@Parameter(names={"-h", "-usage"}, description="How to use it")
	private boolean usage;

	@Parameter(names={"-sysout"}, description="Write in the standard output")
	private boolean sysOut;
	
	@Parameter(names="-threads", description="Number of threads to use")
	private int threads;

	public String getCsv() {
		return csv;
	}
	
	public String getStudy() {
		return study;
	}

	public String getProjectsPath() {
		return projectsPath == null ? "" : projectsPath;
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

	public String getScm() {
		return scm;
	}

	public String getProjectPath() {
		return projectPath == null ? "" : projectPath;
	}
	
	public boolean isUsage() {
		return usage;
	}

	public void setStudy(String study) {
		this.study = study;
	}

	public void setProjectsPath(String projectsPath) {
		this.projectsPath = projectsPath;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public void setCsv(String csv) {
		this.csv = csv;
	}

	public void setScm(String scm) {
		this.scm = scm;
	}

	public void setDetail(boolean detail) {
		this.detail = detail;
	}

	public void setUsage(boolean usage) {
		this.usage = usage;
	}

	public void setThreads(int threads) {
		this.threads = threads;
	}


	public boolean isSysOut() {
		return sysOut;
	}
	
	public void setSysOut(boolean sysOut) {
		this.sysOut = sysOut;
	}
	
}
