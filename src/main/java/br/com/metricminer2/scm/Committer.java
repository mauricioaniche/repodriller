package br.com.metricminer2.scm;

public class Committer {

	private String name;
	private String email;

	public Committer(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}

	
	@Override
	public String toString() {
		return "Committer [name=" + name + ", email=" + email + "]";
	}

	
}
