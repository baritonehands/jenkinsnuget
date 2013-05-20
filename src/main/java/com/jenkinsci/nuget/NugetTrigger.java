/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jenkinsci.nuget;
import antlr.ANTLRException;
import com.jenkinsci.nuget.Utils.NugetUpdater;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.util.FormValidation;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.jenkinsci.lib.xtrigger.AbstractTrigger;
import org.jenkinsci.lib.xtrigger.XTriggerDescriptor;
import org.jenkinsci.lib.xtrigger.XTriggerException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
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
        return "Nuget";
    }

    @Override
    protected Action[] getScheduledActions(Node node, XTriggerLog xtl) {
        return new Action[0];
    }

    @Override
    protected boolean checkIfModified(Node node, XTriggerLog xtl) throws XTriggerException {
        AbstractProject project = (AbstractProject) job;
        NugetUpdater updater = new NugetUpdater(project.getSomeWorkspace(), xtl);
        return updater.performUpdate();
    }

    @Override
    protected String getCause() {
        return "Package updated.";
    }
    
    @Override
    public NugetTriggerDescriptor getDescriptor() {
        return (NugetTriggerDescriptor) Hudson.getInstance().getDescriptorOrDie(getClass());
    }

    @Override
    public Action getProjectAction() {
        return new NugetTriggerAction((AbstractProject)job, getLogFile());
    }
    
    @Extension
    public static final class NugetTriggerDescriptor extends XTriggerDescriptor {
        private URL repo;
        private String apiKey;

        public NugetTriggerDescriptor() {
        }
        
        @DataBoundConstructor
        public NugetTriggerDescriptor(String repo, String apiKey) throws MalformedURLException
        {
            this.repo = new URL(repo);
            this.apiKey = apiKey;
        }
        
        @Override
        public String getDisplayName() {
            return "Build on Nuget updates";
        }
        
        public URL getRepo() {
            return repo;
        }
        
        public String getApiKey() {
            return apiKey;
        }
        
        public FormValidation doCheckRepo(@QueryParameter String repo)
        {
            try {
                new URL(repo);
            } catch (MalformedURLException ex) {
                return FormValidation.error("Invalid url");
            }
            return FormValidation.ok();
        }
    }
}
