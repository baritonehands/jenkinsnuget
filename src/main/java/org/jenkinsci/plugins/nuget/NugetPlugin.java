package org.jenkinsci.plugins.nuget;

import hudson.Plugin;
import hudson.model.Items;
import hudson.model.Run;
import org.jenkins.ui.icon.Icon;
import org.jenkins.ui.icon.IconSet;
import org.jenkins.ui.icon.IconType;
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

    @Override
    public void postInitialize() throws Exception {
        super.postInitialize();
        IconSet.icons.addIcon(new Icon("icon-nuget icon-sm", "nuget/images/16x16/nuget.png", Icon.ICON_SMALL_STYLE, IconType.PLUGIN));
        IconSet.icons.addIcon(new Icon("icon-nuget icon-md", "nuget/images/24x24/nuget.png", Icon.ICON_MEDIUM_STYLE, IconType.PLUGIN));
        IconSet.icons.addIcon(new Icon("icon-nuget icon-lg", "nuget/images/32x32/nuget.png", Icon.ICON_LARGE_STYLE, IconType.PLUGIN));
        IconSet.icons.addIcon(new Icon("icon-nuget icon-xlg", "nuget/images/48x48/nuget.png", Icon.ICON_XLARGE_STYLE, IconType.PLUGIN));
    }
}
