package org.jenkinsci.plugins.nuget;

import hudson.Util;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Arnaud TAMAILLON
 */
public class NugetPublication implements Serializable {
    private final String name;
    private final String url;
    private final Secret apiKey;

    @DataBoundConstructor
    public NugetPublication(String name, String url, String apiKey) {
        this.name = name;
        this.url = url;
        this.apiKey = Secret.fromString(Util.fixEmptyAndTrim(apiKey));
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Secret getApiKey() {
        return apiKey;
    }

    public static final List<NugetPublication> all() {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null) {
            NugetGlobalConfiguration sonarDescriptor = jenkins.getDescriptorByType(NugetGlobalConfiguration.class);
            return sonarDescriptor.getPublications();
        }
        return Collections.EMPTY_LIST;
    }

    public static final NugetPublication get(String name) {
        List<NugetPublication> available = all();
        for (NugetPublication np : available) {
            if (StringUtils.equals(name, np.getName())) {
                return np;
            }
        }
        return null;
    }
}
