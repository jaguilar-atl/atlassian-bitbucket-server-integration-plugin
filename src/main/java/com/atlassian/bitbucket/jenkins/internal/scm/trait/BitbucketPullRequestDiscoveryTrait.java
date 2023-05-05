package com.atlassian.bitbucket.jenkins.internal.scm.trait;

import com.atlassian.bitbucket.jenkins.internal.scm.*;
import hudson.Extension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.trait.SCMBuilder;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.stream.Stream;

public class BitbucketPullRequestDiscoveryTrait extends BitbucketSCMSourceTrait {

    @DataBoundConstructor
    public BitbucketPullRequestDiscoveryTrait() {
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        if (context instanceof BitbucketSCMSourceContext) {
            ((BitbucketSCMSourceContext) context).withDiscoveryHandler(BitbucketDiscoverableHeadType.PULL_REQUEST,
                    new BitbucketSCMHeadDiscoveryHandler() {

                        @Override
                        public Stream<SCMHead> discoverHeads() {
                            return Stream.empty();
                        }

                        @Override
                        public SCMRevision toRevision(SCMHead head) {
                            return BitbucketPullRequestSCMRevision
                                    .fromPullRequestHead((BitbucketPullRequestSCMHead) head);
                        }
                    }
            );
        }
    }

    @Override
    protected void decorateBuilder(SCMBuilder<?, ?> builder) {
        super.decorateBuilder(builder);
    }

    @Extension
    public static class DescriptorImpl extends SCMSourceTraitDescriptor {

        @Override
        public String getDisplayName() {
            return "Bitbucket Server: Discover pull requests";
        }
    }
}
