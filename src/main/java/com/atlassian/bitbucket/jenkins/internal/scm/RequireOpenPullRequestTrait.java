package com.atlassian.bitbucket.jenkins.internal.scm;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;

public class RequireOpenPullRequestTrait extends SCMSourceTrait {

    @NonNull
    @Override
    protected SCMHeadObserver decorateObserver(@NonNull SCMHeadObserver observer) {
        if (observer instanceof PullRequestAwareSCMHeadObserver) {
            ((PullRequestAwareSCMHeadObserver) observer).requiresPullRequests(true);
        }

        return observer;
    }

    @Extension
    public static class DescriptorImpl extends SCMSourceTraitDescriptor {

        @Override
        public String getDisplayName() {
            return "Bitbucket Server: require an open pull request to build";
        }
    }
}
