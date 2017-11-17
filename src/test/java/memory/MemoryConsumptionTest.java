package memory;

import org.junit.Test;
import org.repodriller.RepositoryMining;
import org.repodriller.domain.Commit;
import org.repodriller.filter.range.Commits;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.GitRepository;
import org.repodriller.scm.SCMRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class tests the memory consumption of RepoDriller.
 *
 * @author Mauricio Aniche
 */
public class MemoryConsumptionTest {

    private final String railsPath = this.getClass().getResource("/").getPath() + "../../test-repos/rails";

    @Test
    public void measureMemoryConsumption() {

        if(!runningInTravis())
            return;

        List<Long> max = new ArrayList<>();
        List<Long> min = new ArrayList<>();

        for(int i = 0; i < 10; i++) {
            MemoryVisitor visitor = new MemoryVisitor();

            new RepositoryMining()
                    .in(GitRepository.singleProject(railsPath))
                    .through(Commits.all())
                    .process(visitor)
                    .mine();

            max.add(visitor.maxMemory);
            min.add(visitor.minMemory);
        }

        System.out.println("Max memory (median): " + max.stream().collect(Collectors.averagingLong(d -> d)));
        System.out.println("Min memory (median): " + min.stream().collect(Collectors.averagingLong(d -> d)));
    }

    private boolean runningInTravis () {
        return System.getenv("TRAVIS") != null && System.getenv("TRAVIS").equals("TRUE");
    }

    class MemoryVisitor implements CommitVisitor {

        Runtime runtime = Runtime.getRuntime();

        long maxMemory = Long.MIN_VALUE;
        long minMemory = Long.MAX_VALUE;

        @Override
        public void process (SCMRepository repo, Commit commit, PersistenceMechanism writer) {
            long memory = runtime.totalMemory() - runtime.freeMemory();

            if(memory > maxMemory) maxMemory = memory;
            if(memory < minMemory) minMemory = memory;
        }
    }
}
