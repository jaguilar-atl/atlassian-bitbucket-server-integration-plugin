package com.atlassian.bitbucket.jenkins.internal.scm;

import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.model.TaskListener;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceCriteria;
import jenkins.scm.api.trait.SCMSourceContext;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class BitbucketSCMSourceContext extends SCMSourceContext<BitbucketSCMSourceContext, BitbucketSCMSourceRequest> {

    private final Map<BitbucketDiscoverableHeadType, BitbucketSCMHeadDiscoveryHandler> discoveryHandlers = new HashMap<>();

    public BitbucketSCMSourceContext(@Nullable SCMSourceCriteria criteria, SCMHeadObserver observer) {
        super(criteria, observer);
    }

    public Map<BitbucketDiscoverableHeadType, BitbucketSCMHeadDiscoveryHandler> getDiscoveryHandlers() {
        return discoveryHandlers;
    }

    @Override
    public BitbucketSCMSourceRequest newRequest(SCMSource source, @Nullable TaskListener listener) {
        return new BitbucketSCMSourceRequest(source, this, listener);
    }

    public void withDiscoveryHandler(BitbucketDiscoverableHeadType discoverableHeadType,
                                     BitbucketSCMHeadDiscoveryHandler discoveryHandler) {
        requireNonNull(discoverableHeadType, "discoverableHeadType");
        requireNonNull(discoveryHandler, "discoveryHandler");

        discoveryHandlers.put(discoverableHeadType, discoveryHandler);
    }
}
