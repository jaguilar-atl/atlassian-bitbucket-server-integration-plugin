package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequest;
import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequestRef;
import com.atlassian.bitbucket.jenkins.internal.scm.BitbucketSCMHead;
import com.atlassian.bitbucket.jenkins.internal.scm.BitbucketSCMRevision;
import jenkins.scm.api.SCMRevision;

public class BitbucketPullRequestSCMRevisionFactory {

    public static BitbucketPullRequestSCMRevision create(BitbucketPullRequest pullRequest) {
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

    public static BitbucketPullRequestSCMRevision create(BitbucketPullRequestSCMHead sourceHead) {
        BitbucketSCMHead targetHead = (BitbucketSCMHead) sourceHead.getTarget();
        SCMRevision targetRevision = new BitbucketSCMRevision(targetHead);
        return new BitbucketPullRequestSCMRevision(sourceHead, targetRevision);
    }
}
