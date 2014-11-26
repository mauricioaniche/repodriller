package br.com.metricminer2.scm;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.SourceCodeRepositoryDetailer;

public class SourceCodeRepositoryDetailerTest {

	private PersistenceMechanism ps;
	private String path;

	@Before
	public void setUp() {
		ps = Mockito.mock(PersistenceMechanism.class);
		path = this.getClass().getResource("/repo-1/").getPath();
	}
	
	@Test
	public void shouldDetailARepo() {
		SourceCodeRepositoryDetailer detail = new SourceCodeRepositoryDetailer(ps);
		
		detail.detail(path);
		
		ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
		Mockito.verify(ps).write(captor.capture());
		List<Object> result = captor.getAllValues();
		
		Assert.assertEquals("git@github.com/teste", result.get(1));
		Assert.assertEquals("866e997a9e44cb4ddd9e00efe49361420aff2559", result.get(2));
		Assert.assertEquals("f0dd1308bd904a9b108a6a40865166ee962af3d4", result.get(3));
		Assert.assertEquals(10, result.get(4));
		Assert.assertEquals(0, result.get(5));
		Assert.assertEquals(2, result.get(6));
	}
}
