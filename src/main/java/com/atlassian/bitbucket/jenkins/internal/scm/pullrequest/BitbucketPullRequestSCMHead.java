package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequestState;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.mixin.ChangeRequestSCMHead2;

import static java.util.Objects.requireNonNull;

public class BitbucketPullRequestSCMHead extends SCMHead implements ChangeRequestSCMHead2 {

    private static final String PR_ID_DELIMITER = "/PR#";

    private final String branchName;
    private final String latestCommit;
    private final String pullRequestId;
    private final BitbucketPullRequestState pullRequestState;
    private final SCMHead target;

    public BitbucketPullRequestSCMHead(String branchName, String latestCommit, String pullRequestId,
                                       BitbucketPullRequestState pullRequestState, SCMHead target) {
        super(branchName + PR_ID_DELIMITER + pullRequestId);
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

    public BitbucketPullRequestState getPullRequestState() {
        return pullRequestState;
    }

    @Override
    public SCMHead getTarget() {
        return target;
    }
}
