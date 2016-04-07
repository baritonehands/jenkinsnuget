package org.jenkinsci.plugins.nuget.triggers;
import antlr.ANTLRException;
import hudson.XmlFile;
import hudson.model.Items;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.nuget.Messages;
import org.jenkinsci.plugins.nuget.NugetCause;
import org.jenkinsci.plugins.nuget.Utils.NugetUpdater;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Node;
import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.jenkinsci.lib.xtrigger.AbstractTrigger;
import org.jenkinsci.lib.xtrigger.XTriggerDescriptor;
import org.jenkinsci.lib.xtrigger.XTriggerException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author bgregg
 */
public class NugetTrigger extends AbstractTrigger {
    @DataBoundConstructor
    public NugetTrigger(String cronTabSpec) throws ANTLRException {
        super(cronTabSpec);
    }
    
    @Override
    protected File getLogFile() {
        return new File(job.getRootDir(), "nuget-polling.log");
    }

    @Override
    protected boolean requiresWorkspaceForPolling() {
        return true;
    }

    @Override
    protected String getName() {
        return NugetCause.NAME;
    }

    @Override
    protected Action[] getScheduledActions(Node node, XTriggerLog xtl) {
        return new Action[0];
    }

    @Override
    protected boolean checkIfModified(Node node, XTriggerLog xtl) throws XTriggerException {
        AbstractProject project = (AbstractProject) job;
        NugetUpdater updater = new NugetUpdater(project.getSomeWorkspace(), getDescriptor().nugetExe, xtl);
        return updater.performUpdate();
    }

    @Override
    protected String getCause() {
        return NugetCause.CAUSE;
    }
    
    @Override
    public NugetTriggerDescriptor getDescriptor() {
        return (NugetTriggerDescriptor)super.getDescriptor();
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return Collections.singleton(new NugetTriggerAction(job, getLogFile()));
    }
    
    @Extension
    public static final class NugetTriggerDescriptor extends XTriggerDescriptor {
        private String nugetExe;

        public NugetTriggerDescriptor() {
            super();
            load();
        }
        
        @Override
        public String getDisplayName() {
            return "Build on Nuget updates";
        }

        @Deprecated
        public String getNugetExe() {
            return nugetExe;
        }

        @Override
        public XmlFile getConfigFile() {
            return new XmlFile(Items.XSTREAM2, new File(Jenkins.getInstance().getRootDir(), "com.jenkinsci.nuget.NugetTrigger.xml"));
        }
    }
}
