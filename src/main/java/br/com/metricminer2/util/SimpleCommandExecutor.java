package br.com.metricminer2.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class SimpleCommandExecutor {

	private List<EnvironmentVar> vars = null;
	
	public void setEnvironmentVar(String name, String value)
	{
		if( vars == null )
			vars = new ArrayList<EnvironmentVar>();
		
		vars.add(new EnvironmentVar(name, value));
	}
	
	public String execute(String command, String basePath) {
		StringBuffer total = new StringBuffer();
		String finalCommand = command;
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(finalCommand, getEnvTokens(), new File(basePath));
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
	
	private String[] getEnvTokens()
	{
		if( vars == null )
			return null;
		
		String[] envTokenArray = new String[vars.size()];
		Iterator<EnvironmentVar> envVarIter = vars.iterator();
		int nEnvVarIndex = 0; 
		while (envVarIter.hasNext() == true)
		{
			EnvironmentVar envVar = (EnvironmentVar)(envVarIter.next());
			String envVarToken = envVar.fName + "=" + envVar.fValue;
			envTokenArray[nEnvVarIndex++] = envVarToken;
		}
		
		return envTokenArray;
	}	

}

class EnvironmentVar
{
	public String fName = null;
	public String fValue = null;
	
	public EnvironmentVar(String name, String value)
	{
		fName = name;
		fValue = value;
	}
}