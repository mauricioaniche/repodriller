package org.repodriller.filter.diff;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class OnlyDiffsWithFileTypesTest {

	@Test
	public void shouldAcceptIfFileHasExtensionWithDot() {
		Assert.assertTrue(new OnlyDiffsWithFileTypes(Arrays.asList("cpp", ".java")).accept("/dir/File.java"));
	}
	
	@Test
	public void shouldAcceptIfFileHasExtensionWithoutDot() {
		Assert.assertTrue(new OnlyDiffsWithFileTypes(Arrays.asList(".cpp", "java")).accept("/dir/File.java"));
	}
	
	@Test
	public void shouldRejectIfFileDoesNotMatchExtensions() {
		Assert.assertFalse(new OnlyDiffsWithFileTypes(Arrays.asList("cpp", ".java")).accept("/dir/File.css"));
	}
}
