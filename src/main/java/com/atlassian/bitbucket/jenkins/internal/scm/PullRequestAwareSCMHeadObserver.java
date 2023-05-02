package com.atlassian.bitbucket.jenkins.internal.scm;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequest;
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

    private boolean requiresPullRequests;

    public PullRequestAwareSCMHeadObserver(SCMHeadObserver delegate,
                                           PullRequestRetriever pullRequestRetriever) {
        this.delegate = requireNonNull(delegate, "delegate");
        this.pullRequestRetriever = requireNonNull(pullRequestRetriever, "pullRequestRetriever");
    }

    @Override
    public void observe(@NonNull SCMHead head, @NonNull SCMRevision revision) throws IOException, InterruptedException {
        if (requiresPullRequests) {
            if (head instanceof ChangeRequestSCMHead) {
                // If the head is already a ChangeRequestSCMHead then we simply observe it
                delegate.observe(head, revision);
                return;
            }

            // Otherwise, we check if there are open pull requests for the specified head and convert the head and
            // revision into BitbucketChangeRequestSCMHead and BitbucketChangeRequestSCMRevision respectively
            for (BitbucketPullRequest pullRequest : pullRequestRetriever.getPullRequests(head)) {
                SCMHead targetHead = new SCMHead(pullRequest.getToRef().getDisplayId());
                SCMRevision targetRevision = new BitbucketSCMRevision(targetHead);
                BitbucketChangeRequestSCMHead sourceHead =
                        new BitbucketChangeRequestSCMHead(pullRequest.getFromRef().getDisplayId(),
                                String.valueOf(pullRequest.getId()),
                                targetHead);
                SCMRevision sourceRevision = new BitbucketChangeRequestSCMRevision(sourceHead, targetRevision);
                delegate.observe(sourceHead, sourceRevision);
            }

            return;
        }

        delegate.observe(head, revision);
    }

    public void requiresPullRequests(boolean requiresPullRequests) {
        this.requiresPullRequests = requiresPullRequests;
    }

    public interface PullRequestRetriever {

        Collection<BitbucketPullRequest> getPullRequests(SCMHead head);
    }
}
