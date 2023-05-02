package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import com.atlassian.bitbucket.jenkins.internal.model.BitbucketPullRequest;
import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.mixin.ChangeRequestSCMHead;

import java.io.IOException;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * Custom {@link SCMHeadObserver} that converts {@link SCMRevision revisions} into
 * {@link BitbucketPullRequestSCMRevision pull request revisions} so that the {@link SCMHead} is able to recognize
 * them as such and create the "merge commit" to be used for the build
 */
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

        // Otherwise, we check if there are open pull requests for the specified head and convert the head and revision
        // into BitbucketChangeRequestSCMHead and BitbucketChangeRequestSCMRevision respectively and pass those to the
        // delegate observer so they can be build appropriately
        Collection<BitbucketPullRequest> pullRequests = pullRequestRetriever.getPullRequests(head);
        if (!pullRequests.isEmpty()) {
            for (BitbucketPullRequest pullRequest : pullRequestRetriever.getPullRequests(head)) {
                SCMRevision prRevision = BitbucketPullRequestSCMRevisionFactory.create(pullRequest);
                delegate.observe(prRevision.getHead(), prRevision);
            }
            return;
        }

        // If there are no pull requests, pass it to the delegate observer
        delegate.observe(head, revision);
    }
}
