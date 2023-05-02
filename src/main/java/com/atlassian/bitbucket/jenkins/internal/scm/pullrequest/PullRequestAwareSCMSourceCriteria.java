package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMSourceCriteria;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class PullRequestAwareSCMSourceCriteria implements SCMSourceCriteria {

    private final SCMSourceCriteria delegate;
    private final PullRequestRetriever pullRequestRetriever;

    public PullRequestAwareSCMSourceCriteria(SCMSourceCriteria delegate, PullRequestRetriever pullRequestRetriever) {
        this.delegate = requireNonNull(delegate, "delegate");
        this.pullRequestRetriever = requireNonNull(pullRequestRetriever, "pullRequestRetriever");
    }

    public boolean hasOpenPullRequests(SCMHead head) {
        return !pullRequestRetriever.getPullRequests(head).isEmpty();
    }

    @Override
    public boolean isHead(@NonNull Probe probe, @NonNull TaskListener listener) throws IOException {
        return delegate.isHead(probe, listener);
    }
}
