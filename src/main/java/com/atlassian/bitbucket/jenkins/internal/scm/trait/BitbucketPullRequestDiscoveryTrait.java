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
            SCMRevision revision = gitSCMBuilder.revision();

            if (revision instanceof BitbucketPullRequestSCMRevision) {
                BitbucketPullRequestSCMRevision prRevision = (BitbucketPullRequestSCMRevision) revision;
                BitbucketPullRequestSCMHead prHead = (BitbucketPullRequestSCMHead) prRevision.getHead();
                gitSCMBuilder.withRefSpec("+refs/heads/" + prHead.getOriginName() +
                        ":refs/remotes/@{remote}/" + prHead.getName());

                BitbucketSCMRevision targetRevision = (BitbucketSCMRevision) prRevision.getTarget();
                SCMHead targetHead = targetRevision.getHead();
                gitSCMBuilder.withExtension(new MergeWithGitSCMExtension(targetHead.getName(), targetRevision.getLatestCommit()));
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

        public BitbucketScmHelper getBitbucketScmHelper(String bitbucketUrl, @CheckForNull Credentials httpCredentials) {
            return new BitbucketScmHelper(bitbucketUrl,
                    bitbucketClientFactoryProvider,
                    jenkinsToBitbucketCredentials.toBitbucketCredentials(httpCredentials));
        }

        @Override
        public Class<? extends SCMBuilder> getBuilderClass() {
            return GitSCMBuilder.class;
        }

        public Optional<BitbucketServerConfiguration> getConfiguration(@Nullable String serverId) {
            return bitbucketPluginConfiguration.getServerById(serverId);
        }

        @Override
        public Class<? extends SCMSourceContext> getContextClass() {
            return BitbucketSCMSourceContext.class;
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
