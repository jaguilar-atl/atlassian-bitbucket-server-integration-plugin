package com.atlassian.bitbucket.jenkins.internal.scm;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequestState;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.mixin.ChangeRequestSCMHead2;

import static java.util.Objects.requireNonNull;

public class BitbucketPullRequestSCMHead extends SCMHead implements ChangeRequestSCMHead2 {

    public static final String PR_ID_PREFIX = "pr#";

    private final String branchName;
    private final String latestCommit;
    private final String pullRequestId;
    private final BitbucketPullRequestState pullRequestState;
    private final SCMHead target;

    public BitbucketPullRequestSCMHead(String branchName, String latestCommit, String pullRequestId,
                                       BitbucketPullRequestState pullRequestState, SCMHead target) {
        super(PR_ID_PREFIX + pullRequestId);
        this.branchName = requireNonNull(branchName, "branchName");
        this.latestCommit = requireNonNull(latestCommit, "latestCommit");
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

    public String getLatestCommit() {
        return latestCommit;
    }

    @Override
    public String getOriginName() {
        return branchName;
    }

    @Override
    public SCMHead getTarget() {
        return target;
    }

    public boolean isPullRequestOpen() {
        return pullRequestState == BitbucketPullRequestState.OPEN;
    }
}
