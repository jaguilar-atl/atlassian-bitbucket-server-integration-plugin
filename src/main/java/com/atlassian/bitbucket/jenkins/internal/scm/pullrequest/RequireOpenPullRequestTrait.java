package com.atlassian.bitbucket.jenkins.internal.scm.pullrequest;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.trait.SCMHeadPrefilter;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceTrait;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Optional;

public class RequireOpenPullRequestTrait extends SCMSourceTrait {

    @DataBoundConstructor
    public RequireOpenPullRequestTrait() {
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        Optional<PullRequestAwareSCMSourceCriteria> criteria = context.criteria().stream()
                .filter(PullRequestAwareSCMSourceCriteria.class::isInstance)
                .map(PullRequestAwareSCMSourceCriteria.class::cast)
                .findAny();

        context.withPrefilter(new SCMHeadPrefilter() {
            @Override
            public boolean isExcluded(@NonNull SCMSource source, @NonNull SCMHead head) {
                if (head instanceof BitbucketPullRequestSCMHead) {
                    return ((BitbucketPullRequestSCMHead) head).isPullRequestOpen();
                }

                return criteria.map(criteria -> !criteria.hasOpenPullRequests(head)).orElse(false);
            }
        });
    }

    @Extension
    public static class DescriptorImpl extends SCMSourceTraitDescriptor {

        @Override
        public String getDisplayName() {
            return "Bitbucket Server: require an open pull request to build";
        }
    }
}
