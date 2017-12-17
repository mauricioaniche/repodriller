package org.repodriller.scm;

/**
 * Created by mauricioaniche on 17/12/2017.
 */
public class SCMCollectConfiguration {
    private boolean sourceCode = true;
    private boolean diff = true;
    private boolean branches = true;
    private boolean commitMessages = true;

    public SCMCollectConfiguration sourceCode(boolean config) {
        this.sourceCode = config;
        return this;
    }

    public SCMCollectConfiguration diff(boolean config) {
        this.diff = config;
        return this;
    }

    public SCMCollectConfiguration branches(boolean config) {
        this.branches = config;
        return this;
    }

    public SCMCollectConfiguration commitMessages(boolean config) {
        this.commitMessages = config;
        return this;
    }



    public boolean collectSourceCode () {
        return sourceCode;
    }

    public boolean collectBranches () {
        return branches;
    }

    public boolean collectCommitMessages () {
        return commitMessages;
    }

    public boolean collectDiffs () {
        return diff;
    }
}
