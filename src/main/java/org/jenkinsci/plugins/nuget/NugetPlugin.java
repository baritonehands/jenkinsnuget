package org.jenkinsci.plugins.nuget;

import hudson.Plugin;
import hudson.model.Items;
import hudson.model.Run;
import org.jenkinsci.plugins.nuget.triggers.NugetTrigger;
import org.jenkinsci.plugins.nuget.triggers.NugetTriggerAction;

/**
 * @author Arnaud TAMAILLON
 */
public class NugetPlugin  extends Plugin {
    @Override
    public void start() throws Exception {
        super.start();
        // compatibility with 0.4- versions
        Items.XSTREAM2.addCompatibilityAlias("com.jenkinsci.nuget.NugetTrigger", NugetTrigger.class);
        Items.XSTREAM2.addCompatibilityAlias("com.jenkinsci.nuget.NugetTrigger$NugetTriggerDescriptor", NugetTrigger.NugetTriggerDescriptor.class);
        Run.XSTREAM2.addCompatibilityAlias("com.jenkinsci.nuget.NugetTriggerAction", NugetTriggerAction.class);
    }
}
