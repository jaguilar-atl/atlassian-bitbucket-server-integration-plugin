package com.atlassian.bitbucket.jenkins.internal.scm.trait;

import com.atlassian.bitbucket.jenkins.internal.client.BitbucketClientFactoryProvider;
import com.atlassian.bitbucket.jenkins.internal.config.BitbucketPluginConfiguration;
import com.atlassian.bitbucket.jenkins.internal.config.BitbucketServerConfiguration;
import com.atlassian.bitbucket.jenkins.internal.credentials.JenkinsToBitbucketCredentials;
import com.atlassian.bitbucket.jenkins.internal.scm.*;
import com.cloudbees.plugins.credentials.Credentials;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.plugins.git.GitSCMBuilder;
import jenkins.plugins.git.MergeWithGitSCMExtension;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMHeadCategory;
import jenkins.scm.api.SCMRevision;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.trait.SCMBuilder;
import jenkins.scm.api.trait.SCMSourceContext;
import jenkins.scm.api.trait.SCMSourceTraitDescriptor;
import jenkins.scm.impl.ChangeRequestSCMHeadCategory;
import jenkins.scm.impl.trait.Discovery;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Stream;

import static com.atlassian.bitbucket.jenkins.internal.scm.BitbucketPullRequestSCMHead.PR_ID_PREFIX;

public class BitbucketPullRequestDiscoveryTrait extends BitbucketSCMSourceTrait {

    @DataBoundConstructor
    public BitbucketPullRequestDiscoveryTrait() {
    }

    @Override
    protected void decorateContext(SCMSourceContext<?, ?> context) {
        if (context instanceof BitbucketSCMSourceContext) {
            BitbucketSCMSourceContext bitbucketContext = (BitbucketSCMSourceContext) context;
            BitbucketSCMRepository repository = bitbucketContext.getRepository();
            bitbucketContext.withDiscoveryHandler(
                    BitbucketDiscoverableHeadType.PULL_REQUEST,
                    new BitbucketSCMHeadDiscoveryHandler() {

                        @Override
                        public Stream<SCMHead> discoverHeads() {
                            DescriptorImpl descriptor = (DescriptorImpl) getDescriptor();
                            Optional<BitbucketServerConfiguration> mayBeServerConf =
                                    descriptor.getConfiguration(bitbucketContext.getRepository().getServerId());
                            if (!mayBeServerConf.isPresent()) {
                                return Stream.empty();
                            }

                            BitbucketServerConfiguration serverConfiguration = mayBeServerConf.get();
                            BitbucketScmHelper scmHelper =
                                    descriptor.getBitbucketScmHelper(serverConfiguration.getBaseUrl(),
                                            bitbucketContext.getCredentials().orElse(null));

                            return scmHelper.getOpenPullRequests(repository.getProjectKey(),
                                            repository.getRepositorySlug())
                                    .map(BitbucketPullRequestSCMRevision::fromPullRequest)
                                    .map(BitbucketPullRequestSCMRevision::getHead);
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
        if (builder instanceof GitSCMBuilder) {
            GitSCMBuilder<?> gitSCMBuilder = (GitSCMBuilder<?>) builder;
            gitSCMBuilder.withRefSpec("+refs/pull-requests/*/from:refs/remotes/@{remote}/" + PR_ID_PREFIX + "*");

            SCMHead head = gitSCMBuilder.head();
            if (head instanceof BitbucketPullRequestSCMHead) {
                SCMHead target = ((BitbucketPullRequestSCMHead) head).getTarget();
                gitSCMBuilder.withExtension(new MergeWithGitSCMExtension(target.getName(), "HEAD"));
            }
        }
    }

    @Override
    protected boolean includeCategory(@NonNull SCMHeadCategory category) {
        return category instanceof ChangeRequestSCMHeadCategory;
    }

    @Discovery
    @Extension
    public static class DescriptorImpl extends SCMSourceTraitDescriptor {

        @Inject
        private BitbucketClientFactoryProvider bitbucketClientFactoryProvider;
        @Inject
        private BitbucketPluginConfiguration bitbucketPluginConfiguration;
        @Inject
        private JenkinsToBitbucketCredentials jenkinsToBitbucketCredentials;

        BitbucketScmHelper getBitbucketScmHelper(String bitbucketUrl, @CheckForNull Credentials httpCredentials) {
            return new BitbucketScmHelper(bitbucketUrl,
                    bitbucketClientFactoryProvider,
                    jenkinsToBitbucketCredentials.toBitbucketCredentials(httpCredentials));
        }

        Optional<BitbucketServerConfiguration> getConfiguration(@Nullable String serverId) {
            return bitbucketPluginConfiguration.getServerById(serverId);
        }

        @Override
        public String getDisplayName() {
            return "Bitbucket Server: Discover pull requests";
        }

        @Override
        public boolean isApplicableTo(@NonNull Class<? extends SCMSource> sourceClass) {
            return BitbucketSCMSource.class.isAssignableFrom(sourceClass);
        }
    }
}
