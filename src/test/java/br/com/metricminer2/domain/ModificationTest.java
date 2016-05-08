package br.com.metricminer2.domain;

import org.junit.Assert;
import org.junit.Test;


public class ModificationTest {

	@Test
	public void shouldGetAddsAndRemoves() {
		String diff = "+++ diff info\n"
				+ "+ new line\n"
				+ "- removed line\n"
				+ "+ new line\n"
				+ "\n"
				+ "+ new line\n"
				+ "- removed line\n"
				+ "+++ more git stuff\n"
				+ "--- more git stuff\n";
		
		Modification m = new Modification("old", "new", ModificationType.ADD, diff, "class Java {} ");
		
		Assert.assertEquals(3, m.getAdded());
		Assert.assertEquals(2, m.getRemoved());
	}
	
	@Test
	public void shouldGetNameOfFileEvenWhenDeleted() {
		Modification m1 = new Modification("/a/b/Class.java", "/dev/null", ModificationType.DELETE, "bla bla", "bla bla");
		Assert.assertEquals("Class.java", m1.getFileName());
		
		Modification m2 = new Modification("/a/b/Class.java", "/a/b/Class.java", ModificationType.MODIFY, "bla bla", "bla bla");
		Assert.assertEquals("Class.java", m2.getFileName());

		Modification m3 = new Modification("/dev/null", "/a/b/Class.java", ModificationType.ADD, "bla bla", "bla bla");
		Assert.assertEquals("Class.java", m3.getFileName());
		
	}
}
