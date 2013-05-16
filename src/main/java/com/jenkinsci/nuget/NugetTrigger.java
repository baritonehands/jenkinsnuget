/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jenkinsci.nuget;
import antlr.ANTLRException;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.Node;
import java.io.File;
import org.jenkinsci.lib.xtrigger.AbstractTrigger;
import org.jenkinsci.lib.xtrigger.XTriggerDescriptor;
import org.jenkinsci.lib.xtrigger.XTriggerException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
/**
 *
 * @author bgregg
 */
public class NugetTrigger extends AbstractTrigger {
    public NugetTrigger(String cronTabSpec) throws ANTLRException {
        super(cronTabSpec);
    }
    
    @Override
    protected File getLogFile() {
        return new File(job.getRootDir(), "nuget-polling.log");
    }

    @Override
    protected boolean requiresWorkspaceForPolling() {
        return false;
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
        return false;
    }

    @Override
    protected String getCause() {
        return "Cause?";
    }
    
    @Override
    public NugetTriggerDescriptor getDescriptor() {
        return (NugetTriggerDescriptor) Hudson.getInstance().getDescriptorOrDie(getClass());
    }
    
    @Extension
    public static class NugetTriggerDescriptor extends XTriggerDescriptor {
        @Override
        public String getDisplayName() {
            return "Nuget Trigger";
        }
        
    }
}
