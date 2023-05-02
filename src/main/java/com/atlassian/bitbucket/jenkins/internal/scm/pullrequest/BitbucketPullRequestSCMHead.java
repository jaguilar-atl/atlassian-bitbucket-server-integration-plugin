package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequestState;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.mixin.ChangeRequestSCMHead2;

import static java.util.Objects.requireNonNull;

public class BitbucketPullRequestSCMHead extends SCMHead implements ChangeRequestSCMHead2 {

    private final String pullRequestId;
    private final BitbucketPullRequestState pullRequestState;
    private final SCMHead target;

    public BitbucketPullRequestSCMHead(String name, String pullRequestId,
                                       BitbucketPullRequestState pullRequestState,
                                       SCMHead target) {
        super(name);
        this.pullRequestId = requireNonNull(pullRequestId, "pullRequestId");
        this.pullRequestState = requireNonNull(pullRequestState, "pullRequestState");
        this.target = requireNonNull(target, "target");
    }

    @Override
    public ChangeRequestCheckoutStrategy getCheckoutStrategy() {
        return ChangeRequestCheckoutStrategy.MERGE;
    }

    @Override
    public String getId() {
        return pullRequestId;
    }

    @Override
    public String getOriginName() {
        return getName();
    }

    public BitbucketPullRequestState getPullRequestState() {
        return pullRequestState;
    }

    @Override
    public SCMHead getTarget() {
        return target;
    }
}
