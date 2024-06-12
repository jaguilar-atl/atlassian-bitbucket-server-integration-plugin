package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import jenkins.scm.api.SCMHead;

import java.util.Collection;

/**
 * Retrieves all
 */
public interface PullRequestRevisionRetriever {

    Collection<BitbucketPullRequestSCMRevision> getPullRequestRevisions(SCMHead head);
}
