package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.mixin.ChangeRequestSCMRevision;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class BitbucketPullRequestSCMRevision extends ChangeRequestSCMRevision<BitbucketPullRequestSCMHead> {

    private final String latestCommit;

    public BitbucketPullRequestSCMRevision(BitbucketPullRequestSCMHead head, SCMRevision target) {
        super(head, target);
        this.latestCommit = requireNonNull(head, "head").getLatestCommit();
    }

    @Override
    public boolean equivalent(ChangeRequestSCMRevision<?> revision) {
        if (revision instanceof BitbucketPullRequestSCMRevision) {
            BitbucketPullRequestSCMRevision that = (BitbucketPullRequestSCMRevision) revision;
            return getHead().equals(that.getHead()) && latestCommit.equals(that.latestCommit);
        }

        return false;
    }

    @Override
    protected int _hashCode() {
        return Objects.hash(getHead(), latestCommit);
    }

    @Override
    public String toString() {
        return "merge: " + latestCommit + " + " + getTarget();
    }
}
