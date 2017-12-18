package org.repodriller.scm.git;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.repodriller.domain.Commit;
import org.repodriller.domain.Modification;
import org.repodriller.scm.GitRepository;
import org.repodriller.scm.CollectConfiguration;

import java.io.FileNotFoundException;

/**
 * Created by mauricioaniche on 17/12/2017.
 */
public class GitRepoWithCollectConfigurationTest {

    private static String path1;
    private GitRepository git1;

    @BeforeClass
    public static void readPath() throws FileNotFoundException {
        path1 = GitRepositoryTest.class.getResource("/").getPath() + "../../test-repos/git-1";
    }

    @Before
    public void setUp() {
        git1 = new GitRepository(path1);
    }

    @Test
    public void shouldBringNothingIfConfigured() {

        git1.setDataToCollect(new CollectConfiguration()
                .basicOnly());

        Commit commit = git1.getCommit("a7053a4dcd627f5f4f213dc9aa002eb1caf926f8");

        Assert.assertEquals("", commit.getMsg());
        Assert.assertTrue(commit.getBranches().isEmpty());

        for (Modification modification : commit.getModifications()) {
            Assert.assertEquals("", modification.getDiff());
            Assert.assertEquals("", modification.getSourceCode());
        }
    }

    @Test
    public void shouldBringCommitMessage() {

        git1.setDataToCollect(new CollectConfiguration().commitMessages());

        Commit commit = git1.getCommit("a7053a4dcd627f5f4f213dc9aa002eb1caf926f8");

        Assert.assertNotEquals("", commit.getMsg());
        Assert.assertTrue(commit.getBranches().isEmpty());

        for (Modification modification : commit.getModifications()) {
            Assert.assertEquals("", modification.getDiff());
            Assert.assertEquals("", modification.getSourceCode());
        }
    }

    @Test
    public void shouldBringDiff() {

        git1.setDataToCollect(new CollectConfiguration().diffs());

        Commit commit = git1.getCommit("a7053a4dcd627f5f4f213dc9aa002eb1caf926f8");

        Assert.assertEquals("", commit.getMsg());
        Assert.assertTrue(commit.getBranches().isEmpty());

        for (Modification modification : commit.getModifications()) {
            Assert.assertNotEquals("", modification.getDiff());
            Assert.assertEquals("", modification.getSourceCode());
        }
    }

    @Test
    public void shouldBringSourceCode() {

        git1.setDataToCollect(new CollectConfiguration().sourceCode());

        Commit commit = git1.getCommit("a7053a4dcd627f5f4f213dc9aa002eb1caf926f8");

        Assert.assertEquals("", commit.getMsg());
        Assert.assertTrue(commit.getBranches().isEmpty());

        for (Modification modification : commit.getModifications()) {
            Assert.assertEquals("", modification.getDiff());
            Assert.assertNotEquals("", modification.getSourceCode());
        }
    }

}
