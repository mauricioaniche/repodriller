package org.repodriller.persistence.csv;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import org.junit.Assert;
import org.junit.Before;

public class CSVFileTest {

	private String tmpPath;

	@Before
	public void setUp() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		this.tmpPath = tmpDir + (tmpDir.endsWith(File.separator) ? "" : File.separator) + "test.csv";
	}
	
	@Test
	public void printSimpleCSV() throws IOException {
		CSVFile file = new CSVFile(System.getProperty("java.io.tmpdir"), "test.csv");
		
		file.write("1", "2", "3");
		file.write("4", "5", "6");
		
		String text = new String(Files.readAllBytes(Paths.get(tmpPath)), StandardCharsets.UTF_8);
		String[] lines = text.split(System.getProperty("line.separator"));
		Assert.assertEquals("1,2,3", lines[0]);
		Assert.assertEquals("4,5,6", lines[1]);
	}

	@Test
	public void singleColumn() throws IOException {
		CSVFile file = new CSVFile(System.getProperty("java.io.tmpdir"), "test.csv");
		
		file.write("1");
		file.write("4");
		
		String text = new String(Files.readAllBytes(Paths.get(tmpPath)), StandardCharsets.UTF_8);
		String[] lines = text.split(System.getProperty("line.separator"));
		Assert.assertEquals("1", lines[0]);
		Assert.assertEquals("4", lines[1]);
	}

	@Test
	public void printNulls() throws IOException {
		CSVFile file = new CSVFile(System.getProperty("java.io.tmpdir"), "test.csv");
		
		file.write("1", null, "3");
		file.write("4", "5", "6");
		
		String text = new String(Files.readAllBytes(Paths.get(tmpPath)), StandardCharsets.UTF_8);
		String[] lines = text.split(System.getProperty("line.separator"));
		Assert.assertEquals("1,null,3", lines[0]);
		Assert.assertEquals("4,5,6", lines[1]);
	}

	@Test
	public void printStringsAndOtherTypes() throws IOException {
		CSVFile file = new CSVFile(System.getProperty("java.io.tmpdir"), "test.csv");
		
		file.write(1, null, true);
		file.write(4.55, "mauricio", "'hey'");
		
		String text = new String(Files.readAllBytes(Paths.get(tmpPath)), StandardCharsets.UTF_8);
		String[] lines = text.split(System.getProperty("line.separator"));
		Assert.assertEquals("1,null,true", lines[0]);
		Assert.assertEquals("4.55,mauricio,'hey'", lines[1]);
	}

	@Test
	public void escapeStrings() throws IOException {
		CSVFile file = new CSVFile(System.getProperty("java.io.tmpdir"), "test.csv");
		
		file.write(1, "my name is \"repodriller\", man", "fim");
		
		String text = new String(Files.readAllBytes(Paths.get(tmpPath)), StandardCharsets.UTF_8);
		String[] lines = text.split(System.getProperty("line.separator"));
		Assert.assertEquals("1,\"my name is \"\"repodriller\"\", man\",fim", lines[0]);
	}
	
	@Test
	public void nullHeader() throws IOException {
		CSVFile file = new CSVFile(System.getProperty("java.io.tmpdir"), "test.csv", null);
		
		file.write("0", 10.3, "1");
		
		String text = new String(Files.readAllBytes(Paths.get(tmpPath)), StandardCharsets.UTF_8);
		String[] lines = text.split(System.getProperty("line.separator"));
		Assert.assertEquals("0,10.3,1", lines[0]);
	}
	
	@Test
	public void valuesMatchHeaders() throws IOException {
		String[] header = new String[] {"column1","column2","column3"};
		CSVFile file = new CSVFile(System.getProperty("java.io.tmpdir"), "test.csv", header);
		
		file.write("just","another",1);		

		String text = new String(Files.readAllBytes(Paths.get(tmpPath)), StandardCharsets.UTF_8);
		String[] lines = text.split(System.getProperty("line.separator"));
		Assert.assertEquals("column1, column2, column3", lines[0]);
		Assert.assertEquals("just,another,1", lines[1]);		
	}

	@Test (expected= CSVFileFormatException.class)
	public void valuesDoNotMatchHeaders() throws IOException {
		String[] header = new String[] {"column1","column2"};
		CSVFile file = new CSVFile(System.getProperty("java.io.tmpdir"), "test.csv", header);
		
		file.write("value1");
		
	}
}