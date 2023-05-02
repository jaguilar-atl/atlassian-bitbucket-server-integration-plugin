package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.mixin.ChangeRequestSCMRevision;

import java.util.Objects;

public class BitbucketPullRequestSCMRevision extends ChangeRequestSCMRevision<BitbucketPullRequestSCMHead> {

    public BitbucketPullRequestSCMRevision(@NonNull BitbucketPullRequestSCMHead head,
                                           @NonNull SCMRevision target) {
        super(head, target);
    }

    @Override
    public boolean equivalent(ChangeRequestSCMRevision<?> revision) {
        return Objects.equals(getHead(), revision.getHead());
    }

    @Override
    protected int _hashCode() {
        return getHead().hashCode();
    }
}
