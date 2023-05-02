package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequest;
import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequestRef;
import com.atlassian.bitbucket.jenkins.internal.scm.BitbucketSCMRevision;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;

public class BitbucketPullRequestSCMRevisionFactory {

    public static BitbucketPullRequestSCMRevision create(BitbucketPullRequest pullRequest) {
        BitbucketPullRequestRef fromRef = pullRequest.getFromRef();
        BitbucketPullRequestRef toRef = pullRequest.getToRef();

        SCMHead targetHead = new SCMHead(toRef.getDisplayId());
        SCMRevision targetRevision = new BitbucketSCMRevision(targetHead);
        BitbucketPullRequestSCMHead sourceHead =
                new BitbucketPullRequestSCMHead(fromRef.getDisplayId(),
                        String.valueOf(pullRequest.getId()),
                        pullRequest.getState(),
                        targetHead);
        return new BitbucketPullRequestSCMRevision(sourceHead, targetRevision);
    }
}
