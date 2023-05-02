package com.atlassian.bitbucket.jenkins.internal.scm;

import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.scm.api.SCMRevision;

import static java.util.Objects.requireNonNull;

public class BitbucketSCMRevision extends SCMRevision {

    private final String latestCommit;

    public BitbucketSCMRevision(@NonNull BitbucketSCMHead head) {
        super(head);
        this.latestCommit = requireNonNull(head, "head").getLatestCommit();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BitbucketSCMRevision) {
            BitbucketSCMRevision that = (BitbucketSCMRevision) obj;
            return getHead().equals(that.getHead()) && latestCommit.equals(that.latestCommit);
        }

        return false;
    }

    public String getLatestCommit() {
        return latestCommit;
    }

    @Override
    public int hashCode() {
        return getHead().hashCode();
    }
}
