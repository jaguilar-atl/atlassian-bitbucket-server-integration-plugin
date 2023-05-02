package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequest;
import jenkins.scm.api.SCMHead;

import java.util.Collection;

public interface PullRequestRetriever {

    Collection<BitbucketPullRequest> getPullRequests(SCMHead head);
}
