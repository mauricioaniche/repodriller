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
}
