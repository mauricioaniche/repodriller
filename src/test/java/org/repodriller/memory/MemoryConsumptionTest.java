package org.repodriller.memory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
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
import java.util.Calendar;
import java.util.GregorianCalendar;
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

            long start = System.currentTimeMillis();
            new RepositoryMining()
                    .in(GitRepository.singleProject(railsPath))
                    .through(Commits.range("977b4be208c2c54eeaaf7b46953174ef402f49d4",
                            "ede505592cfab0212e53ca8ad1c38026a7b5d042")) /* 1000 commits */
                    .process(visitor)
                    .mine();
            long end = System.currentTimeMillis();

        System.out.println("Max memory: " + visitor.maxMemory);
        System.out.println("Min memory: " + visitor.minMemory);
        System.out.println("All: " + visitor.all.stream().map(i -> i.toString())
                .collect(Collectors.joining(", ")));

        postGithub(visitor, visitor.numberOfCommits/((end - start)/1000.0));
    }

    private void postGithub(MemoryVisitor visitor, double commitsPerSec) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            String githubUrl = "https://api.github.com/repos/" + System.getenv("TRAVIS_REPO_SLUG") + "/issues/" + System.getenv("TRAVIS_PULL_REQUEST") + "/comments";
            HttpPost httppost = new HttpPost(githubUrl);
            httppost.setHeader("Authorization", "token " + System.getenv("GITHUB_TOKEN"));

            String body = String.format("{\n" + "\"body\": \"" +
                    "Performance stats of your PR:\\n\\n" +
                    "Min memory used    : %.2f MB\\n" +
                    "Max memory used    : %.2f MB\\n" +
                    "Median free memory : %.2f MB\\n" +
                    "Commits per second : %d" +
                    "\"\n}",
                    (visitor.minMemory/1024.0/1024.0),
                    (visitor.maxMemory/1024.0/1024.0),
                    ((visitor.all.stream().collect(Collectors.averagingLong(x -> x)))/1024.0/1024.0),
                    commitsPerSec);

            log.info("body " + body);

            httppost.setEntity(new StringEntity(body));
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            if(entity == null) {
                log.warn("no return from github");
                return;
            }

            InputStream instream = entity.getContent();
            java.util.Scanner s = new java.util.Scanner(instream).useDelimiter("\\A");
            String content = s.hasNext() ? s.next() : "";
            log.info("Response from github:  " + content);
            if(instream != null) instream.close();


        } catch(Exception e) {
            log.warn("Could not post on Github", e);
        }
    }

    private boolean runningInTravis () {
        return System.getenv("TRAVIS") != null && Boolean.parseBoolean(System.getenv("TRAVIS")) == true &&
                System.getenv("TRAVIS_PULL_REQUEST") != null && !System.getenv("TRAVIS_PULL_REQUEST").equals("false");
    }

    class MemoryVisitor implements CommitVisitor {

        Runtime runtime = Runtime.getRuntime();

        long maxMemory = Long.MIN_VALUE;
        long minMemory = Long.MAX_VALUE;
        long numberOfCommits = 0;
        List<Long> all = new ArrayList<>();

        @Override
        public void process (SCMRepository repo, Commit commit, PersistenceMechanism writer) {
            long memory = runtime.totalMemory() - runtime.freeMemory();

            if(memory > maxMemory) maxMemory = memory;
            if(memory < minMemory) minMemory = memory;

            all.add(memory);

            numberOfCommits++;
        }
    }
}
