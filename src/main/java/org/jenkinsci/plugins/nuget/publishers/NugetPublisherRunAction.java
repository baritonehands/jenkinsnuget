package org.jenkinsci.plugins.nuget.publishers;

import com.google.common.collect.Lists;
import hudson.model.BuildBadgeAction;
import hudson.model.Run;
import jenkins.model.RunAction2;

import java.util.List;

/**
 * @author Arnaud TAMAILLON
 */
public class NugetPublisherRunAction implements BuildBadgeAction, RunAction2 {

    private transient Run<?, ?> build;

    private final String name;
    private final List<PublicationResult> packages = Lists.newArrayList();

    public NugetPublisherRunAction(String name, List<PublicationResult> packages) {
        this.name = name;
        this.packages.addAll(packages);
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getUrlName() {
        return null;
    }

    public String getName() {
        return name;
    }

    public List<PublicationResult> getResults() {
        return packages;
    }

    public boolean getHasResults() {
        return getResults().size() > 0;
    }

    @Override
    public void onAttached(Run<?, ?> run) {
        build = run;
    }

    @Override
    public void onLoad(Run<?, ?> run) {
        build = run;
    }
}
