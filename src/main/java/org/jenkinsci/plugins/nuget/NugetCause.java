package org.jenkinsci.plugins.nuget;

import hudson.model.Cause;

/**
 *
 * @author bgregg
 */
public class NugetCause extends Cause {
    public static final String NAME = "NuGet";
    
    @Override
    public String getShortDescription() {
        return String.format("[%s] - %s", NAME, Messages.NugetCause_Cause());
    }
}
