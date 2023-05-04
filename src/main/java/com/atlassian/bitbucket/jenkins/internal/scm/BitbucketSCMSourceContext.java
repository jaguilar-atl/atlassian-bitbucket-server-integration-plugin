package com.atlassian.bitbucket.jenkins.internal.scm;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceCriteria;
import jenkins.scm.api.trait.SCMSourceContext;

public class BitbucketSCMSourceContext extends SCMSourceContext<BitbucketSCMSourceContext, BitbucketSCMSourceRequest> {

    public BitbucketSCMSourceContext(@Nullable SCMSourceCriteria criteria, SCMHeadObserver observer) {
        super(criteria, observer);
    }

    @NonNull
    @Override
    public BitbucketSCMSourceRequest newRequest(SCMSource source, @Nullable TaskListener listener) {
        return new BitbucketSCMSourceRequest(source, this, listener);
    }
}
