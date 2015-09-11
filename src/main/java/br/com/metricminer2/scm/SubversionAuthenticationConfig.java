package br.com.metricminer2.scm;

public class SubversionAuthenticationConfig {

	private String username;
	private String password;

	public SubversionAuthenticationConfig(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}