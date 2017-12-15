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
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringEscapeUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.repodriller.RepoDrillerException;
import org.repodriller.persistence.PersistenceMechanism;

/**
 * A CSVFile lets you write Comma-Separated Value format data to a file
 * using the PersistenceMechanism interface.
 *
 * @author Mauricio Aniche
 */
public class CSVFile implements PersistenceMechanism {

	private PrintStream ps;

	private String[] header = null;

	private boolean isOpen = false;

	private static final Logger log = LogManager.getLogger(CSVFile.class);

	/* Various constructors. */

	/**
	 * A CSVFile named fileName, overwrite existing contents, no header.
	 *
	 * @param fileName
	 */
	public CSVFile(String fileName) {
		this(fileName, false, null);
	}

	/**
	 * A CSVFile named fileName, append?, no header.
	 *
	 * @param fileName
	 * @param append If true we append, else we overwrite
	 */
	public CSVFile(String fileName, boolean append) {
		this(fileName, append, null);
	}

	/**
	 * A CSVFile named fileName, append?, print this header?
	 *
	 * @param fileName
	 * @param append If true we append, else we overwrite
	 * @param header Print if not null
	 */
	public CSVFile(String fileName, boolean append, String[] header){
		open(fileName, append);

		/* Emit the header. */
		this.header = header;
		if (header != null)	{
			log.error("Writing header");
			try {
				write((Object[]) header);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/* Additional weird constructors. */

	public CSVFile(String path, String name) {
		this(path, name, false);
	}

	public CSVFile (String fileName, String[] header) {
		this(fileName, false, header);
	}

	public CSVFile(String path, String name, boolean append) {
		this(Paths.get(path, name).toAbsolutePath().toString(), append, null);
	}

	public CSVFile (String path, String name, String[] header) {
		this(Paths.get(path, name).toAbsolutePath().toString(), false, header);
	}

	public CSVFile(String path, String name, boolean append, String[] header){
		this(Paths.get(path, name).toAbsolutePath().toString(), append, header);
	}

	@Deprecated
	public CSVFile(String fileName, String[] header, boolean append){
		this(fileName, append, header);
	}

	/* Interface PersistenceMechanism. */

	/**
	 * Write a CSV record consisting of these fields.
	 *
	 * @arg fields	Objects with toString()'s. As many fields as are in the header, if you provided one
	 */
	@Override
	public synchronized void write(Object... fields) throws CSVFileFormatException {
		if (!isOpen)
			throw new RepoDrillerException("Error, writing to a closed CSVFile");

		if (header != null && header.length != fields.length)
			throw new CSVFileFormatException("CSV header had " + header.length + " columns but you provided " + fields.length + " fields");

		/* Convert fields to CSV-friendly strings. */
		String[] escapedFields = Stream.of(fields).map(field -> {
			if (field == null)
				return "null";
			else {
				String raw = field.toString();
				if (raw == null)
					return "null";
				return StringEscapeUtils.escapeCsv(raw);
			}
		})
		.toArray(String[]::new);

		/* Out it goes. */
		String csvRecord = String.join(",", escapedFields);
		log.debug("Writing csvRecord: " + csvRecord);
		ps.println(csvRecord);

		ps.flush(); /* TODO This has performance implications. */
	}

	@Override
	public synchronized void close() {
		if (isOpen) {
			ps.close();
			isOpen = false;
		}
	}

	/* Misc. helpers. */

	/**
	 * Open this CSVFile for business.
	 *
	 * @param fileName
	 * @param append
	 */
	private void open(String fileName, boolean append) {
		try {
			ps = new PrintStream(new FileOutputStream(fileName, append));
			isOpen = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
