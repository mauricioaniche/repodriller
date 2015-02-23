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

package br.com.metricminer2.scm;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.metricminer2.SourceCodeRepositoryDetailer;
import br.com.metricminer2.persistence.PersistenceMechanism;

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
