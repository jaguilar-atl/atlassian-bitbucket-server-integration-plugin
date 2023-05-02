package com.atlassian.bitbucket.jenkins.internal.scm;

import com.atlassian.bitbucket.jenkins.internal.scm.pullrequest.BitbucketPullRequestSCMHead;
import com.atlassian.bitbucket.jenkins.internal.scm.pullrequest.BitbucketPullRequestSCMRevision;
import hudson.model.TaskListener;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.GitSCMExtension;
import jenkins.plugins.git.GitSCMBuilder;
import jenkins.plugins.git.GitSCMSource;
import jenkins.plugins.git.MergeWithGitSCMExtension;
import jenkins.scm.api.*;
import jenkins.scm.api.mixin.ChangeRequestSCMHead;

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

    /**
     * This is needed in order so that the {@link GitSCM} can eventually get the merge commit for pull requests.
     * <p>
     * There are two builder methods that we are calling to make this happen.
     * <ul>
     *     <li>{@link GitSCMBuilder#withRefSpec(String)} - Implementations of {@link ChangeRequestSCMHead} use the
     *         "PR id" as the {@link SCMHead#getName() branch name}. This means that when the {@link GitSCM} eventually
     *         tries to look for the current commit for the PR source, it still treats it as a branch and tries look
     *         for the commit using the PR ID as it would using a branch name. To address this we are using a refspec
     *         to map the "local" pr id name to the custom Bitbucket Server pull request refs.</li>
     *     <li>{@link GitSCMBuilder#withExtension(GitSCMExtension)} - We use this to add the
     *         {@link MergeWithGitSCMExtension} which is used by the {@link GitSCM} to create a merge commit to be
     *         used for the actual build.</li>
     * </ul>
     *
     * @param builder
     */
    @Override
    protected void decorate(GitSCMBuilder<?> builder) {
        SCMHead head = builder.head();
        SCMRevision revision = builder.revision();

        if (head instanceof BitbucketPullRequestSCMHead) {
            BitbucketPullRequestSCMHead prHead = (BitbucketPullRequestSCMHead) head;
            builder.withRefSpec("+refs/pull-requests/" + prHead.getId() + "/from:refs/remotes/@{remote}/" + prHead.getName());
        }

        if (revision instanceof BitbucketPullRequestSCMRevision) {
            BitbucketPullRequestSCMRevision prRevision = (BitbucketPullRequestSCMRevision) revision;
            BitbucketSCMRevision targetRevision = (BitbucketSCMRevision) prRevision.getTarget();
            SCMHead targetHead = targetRevision.getHead();
            builder.withExtension(new MergeWithGitSCMExtension(targetHead.getName(), targetRevision.getLatestCommit()));
        }
    }

    public BitbucketSCMRepository getRepository() {
        return repository;
    }
}
