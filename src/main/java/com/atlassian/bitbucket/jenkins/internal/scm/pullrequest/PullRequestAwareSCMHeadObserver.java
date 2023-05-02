package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequest;
import com.atlassian.bitbucket.jenkins.internal.scm.BitbucketSCMRevision;
import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.mixin.ChangeRequestSCMHead;

import java.io.IOException;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class PullRequestAwareSCMHeadObserver extends SCMHeadObserver {

    private final SCMHeadObserver delegate;
    private final PullRequestRetriever pullRequestRetriever;

    public PullRequestAwareSCMHeadObserver(SCMHeadObserver delegate,
                                           PullRequestRetriever pullRequestRetriever) {
        this.delegate = requireNonNull(delegate, "delegate");
        this.pullRequestRetriever = requireNonNull(pullRequestRetriever, "pullRequestRetriever");
    }

    @Override
    public void observe(@NonNull SCMHead head, @NonNull SCMRevision revision) throws IOException, InterruptedException {
        if (head instanceof ChangeRequestSCMHead) {
            // If the head is already a ChangeRequestSCMHead then we simply pass it to the delegate observer
            delegate.observe(head, revision);
            return;
        }

        // Otherwise, we check if there are open pull requests for the specified head and convert the head and
        // revision into BitbucketChangeRequestSCMHead and BitbucketChangeRequestSCMRevision respectively
        Collection<BitbucketPullRequest> pullRequests = pullRequestRetriever.getPullRequests(head);
        if (!pullRequests.isEmpty()) {
            for (BitbucketPullRequest pullRequest : pullRequestRetriever.getPullRequests(head)) {
                SCMHead targetHead = new SCMHead(pullRequest.getToRef().getDisplayId());
                SCMRevision targetRevision = new BitbucketSCMRevision(targetHead);
                BitbucketPullRequestSCMHead sourceHead =
                        new BitbucketPullRequestSCMHead(pullRequest.getFromRef().getDisplayId(),
                                String.valueOf(pullRequest.getId()),
                                targetHead);
                SCMRevision sourceRevision = new BitbucketPullRequestSCMRevision(sourceHead, targetRevision);
                delegate.observe(sourceHead, sourceRevision);
            }
            return;
        }

        // If there are no pull requests, pass it to the delegate observer
        delegate.observe(head, revision);
    }

}
