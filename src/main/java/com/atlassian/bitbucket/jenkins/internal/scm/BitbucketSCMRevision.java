package com.atlassian.bitbucket.jenkins.internal.scm;

import edu.umd.cs.findbugs.annotations.NonNull;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;

import java.util.Objects;

public class BitbucketSCMRevision extends SCMRevision {

    public BitbucketSCMRevision(@NonNull SCMHead head) {
        super(head);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SCMRevision) {
            SCMRevision that = (SCMRevision) obj;
            return Objects.equals(getHead(), that.getHead());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getHead().hashCode();
    }
}
