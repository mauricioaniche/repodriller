package br.com.metricminer2.domain;

import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.git.GitRepositoryTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;

public class CommitTest {

    private static String path;
    private GitRepository git;

    private Commit c1;
    private Commit c2;
    private Commit c3;

    @BeforeClass
    public static void readPath() throws FileNotFoundException {
        path = GitRepositoryTest.class.getResource("/").getPath() + "../../test-repos/git-1";

    }

    @Before
    public void setUp() {
        git = new GitRepository(path);
        c1 = git.getCommit("e7d13b0511f8a176284ce4f92ed8c6e8d09c77f2");
        c2 = git.getCommit(c1.getParent());
        c3 = git.getCommit("a4ece0762e797d2e2dcbd471115108dd6e05ff58");
    }

    @Test
    public void shouldBeEqual() {
        Assert.assertEquals(c1.getParent(), "a4ece0762e797d2e2dcbd471115108dd6e05ff58");
        Assert.assertEquals(c2, c3);
    }

    @Test
    public void shouldNotBeEqual() {
        Assert.assertEquals(c1.getParent(), "a4ece0762e797d2e2dcbd471115108dd6e05ff58");
        Assert.assertNotEquals(c1, c3);
    }
}
