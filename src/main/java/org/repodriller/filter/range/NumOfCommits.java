package org.repodriller.filter.range;

import org.repodriller.domain.ChangeSet;
import org.repodriller.scm.SCM;

import java.util.List;
public class NumOfCommits implements CommitRange {
    int numberOfCommits;

    public NumOfCommits(int numberOfCommits){
        this.numberOfCommits = numberOfCommits;
    }

    @Override
    public List<ChangeSet> get(SCM scm) {
        List<ChangeSet> all = scm.getChangeSets();
        if (all.size() < this.numberOfCommits)
            return all;
        return all.subList(0, this.numberOfCommits);
    }
}
