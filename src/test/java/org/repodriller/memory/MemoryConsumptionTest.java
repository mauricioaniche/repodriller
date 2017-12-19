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
import org.repodriller.scm.CollectConfiguration;
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

        // collecting everything
        MemoryVisitor collectEverythingVisitor = new MemoryVisitor();
        long start1 = System.currentTimeMillis();
        new RepositoryMining()
                .in(GitRepository.singleProject(railsPath))
                .through(Commits.range("977b4be208c2c54eeaaf7b46953174ef402f49d4",
                        "ede505592cfab0212e53ca8ad1c38026a7b5d042")) /* 1000 commits */
                .process(collectEverythingVisitor)
                .mine();
        long end1 = System.currentTimeMillis();

        System.gc();

        // collecting nothing
        MemoryVisitor collectNothingVisitor = new MemoryVisitor();
        long start2 = System.currentTimeMillis();
        new RepositoryMining()
                .in(GitRepository.singleProject(railsPath))
                .through(Commits.range("977b4be208c2c54eeaaf7b46953174ef402f49d4",
                        "ede505592cfab0212e53ca8ad1c38026a7b5d042")) /* 1000 commits */
                .collect(new CollectConfiguration().basicOnly())
                .process(collectNothingVisitor)
                .mine();
        long end2 = System.currentTimeMillis();

        // printing info
        System.out.println("When collecting everything:");
        System.out.println("Max memory: " + collectEverythingVisitor.maxMemory);
        System.out.println("Min memory: " + collectEverythingVisitor.minMemory);
        System.out.println("All: " + collectEverythingVisitor.all.stream().map(i -> i.toString())
                .collect(Collectors.joining(", ")));

        System.out.println("When collecting nothing:");
        System.out.println("Max memory: " + collectNothingVisitor.maxMemory);
        System.out.println("Min memory: " + collectNothingVisitor.minMemory);
        System.out.println("All: " + collectNothingVisitor.all.stream().map(i -> i.toString())
                .collect(Collectors.joining(", ")));

        postGithub(collectEverythingVisitor, collectEverythingVisitor.numberOfCommits/((end1 - start1)/1000.0),
                collectNothingVisitor, collectNothingVisitor.numberOfCommits/((end2 - start2)/1000.0));
    }

    private void postGithub(MemoryVisitor collectEverythingVisitor, double commitsPerSecInEverything, MemoryVisitor collectNothingVisitor, double commitsPerSecInNothing) {
        try {
            HttpClient httpclient = HttpClients.createDefault();
            String githubUrl = "https://api.github.com/repos/" + System.getenv("TRAVIS_REPO_SLUG") + "/issues/" + System.getenv("TRAVIS_PULL_REQUEST") + "/comments";
            HttpPost httppost = new HttpPost(githubUrl);
            httppost.setHeader("Authorization", "token " + System.getenv("GITHUB_TOKEN"));

            String body = String.format("{\n" + "\"body\": \"" +
                    "Performance stats of your PR:\\n\\n" +
                    "When collecting everything:\\n" +
                    "Min memory used    : %.2f MB\\n" +
                    "Max memory used    : %.2f MB\\n" +
                    "Median free memory : %.2f MB\\n" +
                    "Commits per second : %.2f\\n\\n" +
                    "When collecting nothing:\\n" +
                    "Min memory used    : %.2f MB\\n" +
                    "Max memory used    : %.2f MB\\n" +
                    "Median free memory : %.2f MB\\n" +
                    "Commits per second : %.2f\\n\\n" +
                    "\"\n}",
                    (collectEverythingVisitor.minMemory/1024.0/1024.0),
                    (collectEverythingVisitor.maxMemory/1024.0/1024.0),
                    ((collectEverythingVisitor.all.stream().collect(Collectors.averagingLong(x -> x)))/1024.0/1024.0),
                    commitsPerSecInEverything,
                    (collectNothingVisitor.minMemory/1024.0/1024.0),
                    (collectNothingVisitor.maxMemory/1024.0/1024.0),
                    ((collectNothingVisitor.all.stream().collect(Collectors.averagingLong(x -> x)))/1024.0/1024.0),
                    commitsPerSecInNothing
                    );

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
