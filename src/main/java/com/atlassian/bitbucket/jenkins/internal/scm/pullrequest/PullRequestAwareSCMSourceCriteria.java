package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMSourceCriteria;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class PullRequestAwareSCMSourceCriteria implements SCMSourceCriteria {

    private final SCMSourceCriteria delegate;
    private final PullRequestRevisionRetriever revisionRetriever;

    public PullRequestAwareSCMSourceCriteria(SCMSourceCriteria delegate, PullRequestRevisionRetriever revisionRetriever) {
        this.delegate = requireNonNull(delegate, "delegate");
        this.revisionRetriever = requireNonNull(revisionRetriever, "revisionRetriever");
    }

    public boolean hasOpenPullRequests(SCMHead head) {
        return revisionRetriever.getPullRequestRevisions(head).stream()
                .filter(BitbucketPullRequestSCMRevision::isPullRequestOpen)
                .count() > 0;
    }

    @Override
    public boolean isHead(@NonNull Probe probe, @NonNull TaskListener listener) throws IOException {
        return delegate.isHead(probe, listener);
    }
}
