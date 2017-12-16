package memory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.domain.Commit;
import org.repodriller.filter.range.Commits;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.GitRepository;
import org.repodriller.scm.SCMRepository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class tests the memory consumption of RepoDriller.
 *
 * @author Mauricio Aniche
 */
public class MemoryConsumptionTest {

    private static Logger log = LogManager.getLogger(MemoryConsumptionTest.class);

    private final String railsPath = this.getClass().getResource("/").getPath() + "../../test-repos/rails";

    @Test
    public void measureMemoryConsumption() {

        if(!runningInTravis())
            return;

            MemoryVisitor visitor = new MemoryVisitor();


            new RepositoryMining()
                    .in(GitRepository.singleProject(railsPath))
                    .through(Commits.range("977b4be208c2c54eeaaf7b46953174ef402f49d4",
                            "ede505592cfab0212e53ca8ad1c38026a7b5d042")) /* 1000 commits */
                    .process(visitor)
                    .mine();

        System.out.println("Max memory (median): " + visitor.maxMemory);
        System.out.println("Min memory (median): " + visitor.minMemory);
        System.out.println("All: " + visitor.all.stream().map(i -> i.toString())
                .collect(Collectors.joining(", ")));

        postGithub(visitor);
    }

    private void postGithub(MemoryVisitor visitor) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("https://api.github.com/repos/" + System.getenv("TRAVIS_REPO_SLUG") + "/issues/" + System.getenv("TRAVIS_PULL_REQUEST") + "/comments");
            httppost.setHeader("Authorization", "token " + System.getenv("GITHUB_TOKEN"));

            String allMeasuments = visitor.all.stream().map(i -> i.toString())
                    .collect(Collectors.joining(", "));

            httppost.setEntity(new StringEntity(
                    "{\n" + "body: \"" +
                    "Min memory: " + visitor.minMemory + "\\n" +
                    "Max memory: " + visitor.maxMemory + "\\n" +
                    "All measurements: " + allMeasuments +
                    "\"}"));
            httpclient.execute(httppost);
        } catch(Exception e) {
            log.warn("Could not post on Github", e);
        }
    }

    private boolean runningInTravis () {
        return System.getenv("TRAVIS") != null && Boolean.parseBoolean(System.getenv("TRAVIS")) == true &&
                System.getenv("TRAVIS_PULL_REQUEST") != null;
    }

    class MemoryVisitor implements CommitVisitor {

        Runtime runtime = Runtime.getRuntime();

        long maxMemory = Long.MIN_VALUE;
        long minMemory = Long.MAX_VALUE;
        List<Long> all = new ArrayList<>();

        @Override
        public void process (SCMRepository repo, Commit commit, PersistenceMechanism writer) {
            long memory = runtime.totalMemory() - runtime.freeMemory();

            if(memory > maxMemory) maxMemory = memory;
            if(memory < minMemory) minMemory = memory;

            all.add(memory);
        }
    }
}
