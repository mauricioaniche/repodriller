package org.repodriller.filter.diff;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class OnlyDiffsWithoutFileTypesTest {

	@Test
	public void shouldRejectIfFileHasExtensionWithDot() {
		Assert.assertFalse(new OnlyDiffsWithoutFileTypes(Arrays.asList("cpp", ".java")).accept("/dir/File.java"));
	}
	
	@Test
	public void shouldRejectIfFileHasExtensionWithoutDot() {
		Assert.assertFalse(new OnlyDiffsWithoutFileTypes(Arrays.asList(".cpp", "java")).accept("/dir/File.java"));
	}
	
	@Test
	public void shouldAcceptIfFileDoesNotMatchExtensions() {
		Assert.assertTrue(new OnlyDiffsWithoutFileTypes(Arrays.asList("cpp", ".java")).accept("/dir/File.css"));
	}
}
