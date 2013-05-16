/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jenkinsci.nuget;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import java.io.IOException;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 *
 * @author bgregg
 */
public class NugetBuild extends Builder {
    
    private String target;
    private boolean publish = true;
    
    @DataBoundConstructor
    public NugetBuild(String target, boolean publish)
    {
        this.target = target;
        this.publish = publish;
    }
    
    public String getTarget() {
        return target;
    }
    
    public boolean getPublish() {
        return publish;
    }
    
    @Override
    public NugetBuildDescriptor getDescriptor() {
        return (NugetBuildDescriptor)super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        
        return true;
    }
    
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class NugetBuildDescriptor extends BuildStepDescriptor<Builder> {

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Nuget";
        }
        
        public FormValidation doCheckTarget(@QueryParameter String target) {
            if(target.length() > 0) {
                String[] endings = { ".csproj", ".vbproj", ".nuspec" };
                for(String ending : endings) {
                    if(target.endsWith(ending))
                        return FormValidation.ok();
                }
                return FormValidation.error("Target must end with .csproj, .vbproj, or .nuspec.");
            }
            return FormValidation.error("Target is required.");
        }
    }
}
