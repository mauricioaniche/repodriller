package org.repodriller.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

/**
 * If your analysis needs to work with external tools, a SimpleCommandExecutor gives you a clean way to interact with them.
 * Usage:
 *  - Set environment vars with the various manipulation methods.
 *  - Execute with {@link SimpleCommandExecutor#execute}.
 *
 * @author Mauricio Aniche
 */
public class SimpleCommandExecutor {

	private List<EnvironmentVar> envVars = null;
	private boolean inheritEnv = false;

	private static Logger log = Logger.getLogger(SimpleCommandExecutor.class);

	public SimpleCommandExecutor() {
		envVars = new ArrayList<EnvironmentVar>();
		inheritEnv = false;
	}

	/**
	 * Should child inherit parent's environment vars?
	 *
	 * @param inherit	True for inherit, else false.
	 * @return 			this, for chaining
	 */
	public SimpleCommandExecutor inheritEnv(boolean inherit) {
		inheritEnv = inherit;
		return this;
	}

	/**
	 * Add this <name, value> pair to the child's environment.
	 *
	 * @param name The name of the parameter
	 * @param value Its value
	 * @return this, for chaining
	 */
	public SimpleCommandExecutor setEnvironmentVar(String name, String value) {
		envVars.add(new EnvironmentVar(name, value));
		return this;
	}

	/**
	 * Clear the child's environment.
	 *
	 * @return this, for chaining
	 */
	public SimpleCommandExecutor clearEnvironmentVars() {
		envVars.clear();
		return this;
	}

	/**
	 * Execute {@code command} from {@code workDir}, providing the specified environment.
	 * For example, {@code execute("ls /tmp", null)}.
	 *
	 * @param command	The command to invoke
	 * @param workDir	Working dir from which to execute the command
	 * @return			The output from the command
	 */
	public String execute(String command, String workDir) {
		StringBuffer total = new StringBuffer();
		Process proc;
		try {
			log.debug("Executing command <" + command + "> in workDir " + workDir);
			File wd = workDir == null ? null : new File(workDir);
			proc = Runtime.getRuntime().exec(command, getEnvTokens(), wd);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Scanner sc = new Scanner(proc.getInputStream());
		while (sc.hasNextLine()) {
			total.append(sc.nextLine() + "\r\n");
		}
		sc.close();

		return total.toString();
	}

	/**
	 * If inheritEnv, returns null.
	 * Otherwise returns a properly-formatted array of environment tokens in "X=Y" format.
	 *
	 * @return Environment variables as tokens suitable for use with Runtime.exec
	 */
	private String[] getEnvTokens() {
		if (inheritEnv)
			return null;

		String[] envTokens = new String[envVars.size()];

		/* Convert envVars to envTokens. */
		Iterator<EnvironmentVar> iter = envVars.iterator();
		int i = 0;
		while (iter.hasNext()) {
			EnvironmentVar envVar = iter.next();
			String envVarToken = String.join("=", envVar.fName + "=" + envVar.fValue);
			envTokens[i] = envVarToken;
			i++;
		}

		return envTokens;
	}

}

/**
 * Simple wrapper for the <key, value> pairs set in the child's environment.
 * There's really no purpose to this class at the moment, but maybe we'll want it later.
 *
 * @author Mauricio Aniche
 */
class EnvironmentVar {
	public String fName = null;
	public String fValue = null;

	public EnvironmentVar(String name, String value) {
		fName = name;
		fValue = value;
	}
}