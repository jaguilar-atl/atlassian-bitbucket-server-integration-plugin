package com.atlassian.bitbucket.jenkins.internal.scm;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequest;
import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequestRef;
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

    public static BitbucketPullRequestSCMRevision fromPullRequest(BitbucketPullRequest pullRequest) {
        requireNonNull(pullRequest, "pullRequest");

        BitbucketPullRequestRef fromRef = pullRequest.getFromRef();
        BitbucketPullRequestRef toRef = pullRequest.getToRef();

        BitbucketSCMHead targetHead = new BitbucketSCMHead(toRef.getDisplayId(), toRef.getLatestCommit());
        SCMRevision targetRevision = new BitbucketSCMRevision(targetHead);
        BitbucketPullRequestSCMHead sourceHead =
                new BitbucketPullRequestSCMHead(fromRef.getDisplayId(),
                        fromRef.getLatestCommit(),
                        String.valueOf(pullRequest.getId()),
                        pullRequest.getState(),
                        targetHead);
        return new BitbucketPullRequestSCMRevision(sourceHead, targetRevision);
    }

    public static BitbucketPullRequestSCMRevision fromPullRequestHead(BitbucketPullRequestSCMHead prHead) {
        requireNonNull(prHead, "prHead");
        BitbucketSCMHead targetHead = (BitbucketSCMHead) prHead.getTarget();
        SCMRevision targetRevision = new BitbucketSCMRevision(targetHead);
        return new BitbucketPullRequestSCMRevision(prHead, targetRevision);
    }

    @Override
    public boolean equivalent(ChangeRequestSCMRevision<?> revision) {
        if (revision instanceof BitbucketPullRequestSCMRevision) {
            BitbucketPullRequestSCMRevision that = (BitbucketPullRequestSCMRevision) revision;
            return getHead().equals(that.getHead()) && latestCommit.equals(that.latestCommit);
        }

        return false;
    }

    public boolean isPullRequestOpen() {
        return ((BitbucketPullRequestSCMHead) getHead()).isPullRequestOpen();
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
