package com.atlassian.bitbucket.jenkins.internal.scm;

import com.atlassian.bitbucket.jenkins.internal.client.BitbucketFilePathClient;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.SCMProbe;
import jenkins.scm.api.SCMProbeStat;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

public class BitbucketSCMProbe extends SCMProbe {

    private final BitbucketFilePathClient filePathClient;
    private final SCMHead head;

    public BitbucketSCMProbe(SCMHead head, BitbucketFilePathClient filePathClient) {
        this.filePathClient = requireNonNull(filePathClient, "filePathClient");
        this.head = requireNonNull(head, "head");
    }

    @Override
    public String name() {
        return head.getName();
    }

    @Override
    public long lastModified() {
        // This is supposed to indicate when the head was last modified (e.g. the most recent commit timestamp).
        // However, we are not using it directly, so we're just using 0 for now.
        return 0L;
    }

    @Override
    public SCMProbeStat stat(String path) throws IOException {
        requireNonNull(path, "path");

        return SCMProbeStat.fromType(filePathClient.getFileType(path, getRef()));
    }

    @Override
    public void close() throws IOException {
    }

    private String getRef() {
        if (head instanceof BitbucketPullRequestSCMHead) {
            return ((BitbucketPullRequestSCMHead) head).getOriginName();
        }

        return head.getName();
    }
}
