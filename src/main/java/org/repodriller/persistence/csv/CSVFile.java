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

package org.repodriller.persistence.csv;

import java.io.FileOutputStream;
import java.io.PrintStream;
import org.apache.commons.lang3.StringEscapeUtils;

import org.repodriller.persistence.PersistenceMechanism;

public class CSVFile implements PersistenceMechanism {

	private PrintStream ps;

	public CSVFile(String fileName) {
		this(fileName, false);
	}

	public CSVFile(String fileName, boolean append) {
		try {
			ps = new PrintStream(new FileOutputStream(fileName, append));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public CSVFile(String path, String name) {
		this(path, name, false);
	}

	public CSVFile(String path, String name, boolean append) {
		this(verifyPath(path) + name, append);
	}

	@Override
	public synchronized void write(Object... line) {
		boolean first = true;
		for(Object o : line) {
			if(!first) ps.print(",");

			if(o==null) ps.print("null");
			else {
				String field = o.toString();
				field = StringEscapeUtils.escapeCsv(field);
				ps.print(field);
			}
			first=false;
		}
		
		ps.println();
		ps.flush();

	}
	
	public void close() {
		ps.close();
	}
	
	private static String verifyPath(String path) {
		char lastchar = path.charAt(path.length()-1);
		if (lastchar != '/')
			path = path + '/';
		return path;		
	}
}
