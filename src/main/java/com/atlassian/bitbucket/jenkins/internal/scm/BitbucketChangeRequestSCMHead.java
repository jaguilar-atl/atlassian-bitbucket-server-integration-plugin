package com.atlassian.bitbucket.jenkins.internal.scm;

import jenkins.scm.api.SCMHead;
import jenkins.scm.api.mixin.ChangeRequestCheckoutStrategy;
import jenkins.scm.api.mixin.ChangeRequestSCMHead2;

import static java.util.Objects.requireNonNull;

public class BitbucketChangeRequestSCMHead extends SCMHead implements ChangeRequestSCMHead2 {

    private final String pullRequestId;
    private final SCMHead target;

    public BitbucketChangeRequestSCMHead(String name, String pullRequestId, SCMHead target) {
        super(name);
        this.pullRequestId = requireNonNull(pullRequestId, "pullRequestId");
        this.target = requireNonNull(target, "target");
    }

    @Override
    public ChangeRequestCheckoutStrategy getCheckoutStrategy() {
        return ChangeRequestCheckoutStrategy.MERGE;
    }

    @Override
    public String getOriginName() {
        return getName();
    }

    @Override
    public String getId() {
        return pullRequestId;
    }

    @Override
    public SCMHead getTarget() {
        return target;
    }
}
