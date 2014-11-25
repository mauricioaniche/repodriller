package br.com.metricminer2;

import com.beust.jcommander.Parameter;


public class MMOptions {

	@Parameter(names="-study", description="Class with the study definition")
	private String study;
	
	@Parameter(names="-projects", description="Path to the projects that will be analyzed")
	private String projects;

	@Parameter(names="-csv", description="Path to the CSV that will be outputed")
	private String csv;
	
	@Parameter(names="-detail", description="Give details from all projects")
	private boolean detail;

	public String getCsv() {
		return csv;
	}
	
	public String getStudy() {
		return study;
	}

	public String getProjects() {
		return projects;
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
		return "MMOptions [study=" + study + ", projects=" + projects + ", csv=" + csv + ", detail=" + detail + "]";
	}

	
	
}
