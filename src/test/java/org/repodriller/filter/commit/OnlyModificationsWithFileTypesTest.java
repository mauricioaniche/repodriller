package org.repodriller.filter.commit;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.repodriller.domain.ChangeSet;
import org.repodriller.domain.Commit;
import org.repodriller.domain.CommitContributor;
import org.repodriller.domain.Modification;
import org.repodriller.domain.ModificationType;

public class OnlyModificationsWithFileTypesTest {

	@Test
	public void shouldAcceptIfCommitContainsFilesWithThatType() {
		Set<String> branches = new HashSet<>();
		branches.add("master");
		branches.add("b1");

		Calendar d = Calendar.getInstance();
		CommitContributor mau = new CommitContributor("Mau", "mau@mau.com", d);
		ChangeSet cs = new ChangeSet("123", "foo", mau, null, null, branches, true);
		Commit commit = new Commit(cs);
		commit.addModification(new Modification("/dir/File.java", "/dir/File.java", ModificationType.MODIFY, "aaa", "bla"));
		commit.addModification(new Modification("/dir/File.css", "/dir/File.css", ModificationType.MODIFY, "aaa", "bla"));

		Assert.assertTrue(new OnlyModificationsWithFileTypes(Arrays.asList(".java")).accept(commit));

		Commit commit2 = new Commit(cs);
		commit2.addModification(new Modification("/dir/File.js", "/dir/File.js", ModificationType.MODIFY, "aaa", "bla"));
		commit2.addModification(new Modification("/dir/File.css", "/dir/File.css", ModificationType.MODIFY, "aaa", "bla"));
		Assert.assertFalse(new OnlyModificationsWithFileTypes(Arrays.asList(".java")).accept(commit2));
	}
}
