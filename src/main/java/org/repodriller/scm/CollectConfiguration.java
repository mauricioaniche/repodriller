package org.repodriller.scm;

/**
 * Created by mauricioaniche on 17/12/2017.
 */
public class CollectConfiguration {
    private boolean sourceCode = false;
    private boolean diff = false;
    private boolean branches = false;
    private boolean commitMessages = false;

    public CollectConfiguration sourceCode() {
        this.sourceCode = true;
        return this;
    }

    public CollectConfiguration diffs () {
        this.diff = true;
        return this;
    }

    public CollectConfiguration branches() {
        this.branches = true;
        return this;
    }

    public CollectConfiguration commitMessages() {
        this.commitMessages = true;
        return this;
    }

    public CollectConfiguration basicOnly() {
        sourceCode = false;
        diff = false;
        branches = false;
        commitMessages = false;

        return this;
    }

    public boolean isCollectingSourceCode () {
        return sourceCode;
    }

    public boolean isCollectingBranches () {
        return branches;
    }

    public boolean isCollectingCommitMessages () {
        return commitMessages;
    }

    public boolean isCollectingDiffs () {
        return diff;
    }
}
