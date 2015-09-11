package br.com.metricminer2.scm;

public class SubversionConfig {

	private String repositoryPath;
	private String workingCopyPath;
	private SubversionAuthenticationConfig subversionAuth;

	public SubversionConfig(String repositoryPath, String workingCopyPath) {
		this(repositoryPath, workingCopyPath, null);
	}

	public SubversionConfig(String repositoryPath, String workingCopyPath, SubversionAuthenticationConfig subversionAuth) {
		this.repositoryPath = repositoryPath;
		this.workingCopyPath = workingCopyPath;
		this.subversionAuth = subversionAuth;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public String getWorkingCopyPath() {
		return workingCopyPath;
	}

	public String getUsername() {
		return subversionAuth.getUsername();
	}

	public String getPassword() {
		return subversionAuth.getPassword();
	}

	public SubversionAuthenticationConfig getSubversionAuth() {
		return subversionAuth;
	}
}