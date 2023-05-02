package com.atlassian.bitbucket.jenkins.internal.scm;

import com.atlassian.bitbucket.jenkins.internal.scm.pullrequest.BitbucketPullRequestSCMRevision;
import hudson.model.TaskListener;
import jenkins.plugins.git.GitSCMBuilder;
import jenkins.plugins.git.GitSCMSource;
import jenkins.plugins.git.MergeWithGitSCMExtension;
import jenkins.scm.api.*;

import javax.annotation.CheckForNull;
import java.io.IOException;

/**
 * This class exists to work around the following issue:
 * 1. we do not want to re-implement the retrieve found in the {@link GitSCMSource},
 * however it is protected so we can't access it from our class.
 * 2. in SelectBranchTrait, we require access to the repository and SelectBranchTrait can't use BitbucketSCMSource's selectTrait
 * since it is not implemented
 * <p>
 * This class inherits from the {@link GitSCMSource} and thus can access it and expose a method wrapper.
 */
class CustomGitSCMSource extends GitSCMSource {

    private BitbucketSCMRepository repository;

    public CustomGitSCMSource(String remote, BitbucketSCMRepository repository) {
        super(remote);
        this.repository = repository;
    }

    public void accessibleRetrieve(@CheckForNull SCMSourceCriteria criteria, SCMHeadObserver observer,
                                   @CheckForNull SCMHeadEvent<?> event,
                                   TaskListener listener) throws IOException, InterruptedException {
        super.retrieve(criteria, observer, event, listener);
    }

    public SCMRevision accessibleRetrieve(SCMHead head, TaskListener listener) throws IOException, InterruptedException {
        return super.retrieve(head, listener);
    }

    public BitbucketSCMRepository getRepository() {
        return repository;
    }

    @Override
    protected GitSCMBuilder<?> newBuilder(SCMHead head, SCMRevision revision) {
        GitSCMBuilder<?> builder = super.newBuilder(head, revision);

        if (revision instanceof BitbucketPullRequestSCMRevision) {
            BitbucketPullRequestSCMRevision prRevision = (BitbucketPullRequestSCMRevision) revision;
            BitbucketSCMRevision targetRevision = (BitbucketSCMRevision) prRevision.getTarget();
            SCMHead targetHead = targetRevision.getHead();
            builder.withExtension(new MergeWithGitSCMExtension(targetHead.getName(), targetRevision.getLatestCommit()));
        }

        return builder;
    }
}
