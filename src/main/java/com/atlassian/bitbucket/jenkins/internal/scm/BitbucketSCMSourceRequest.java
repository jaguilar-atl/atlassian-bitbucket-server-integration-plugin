package com.atlassian.bitbucket.jenkins.internal.scm;

import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.trait.SCMSourceRequest;

public class BitbucketSCMSourceRequest extends SCMSourceRequest {

    protected BitbucketSCMSourceRequest(SCMSource source,
                                        BitbucketSCMSourceContext context,
                                        @Nullable TaskListener listener) {
        super(source, context, listener);
    }
}
