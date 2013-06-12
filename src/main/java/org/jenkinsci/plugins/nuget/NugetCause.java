/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jenkinsci.plugins.nuget;

import hudson.model.Cause;

/**
 *
 * @author bgregg
 */
public class NugetCause extends Cause {
    public static final String NAME = "NuGet";
    public static final String CAUSE = "A package has been updated";
    
    @Override
    public String getShortDescription() {
        return String.format("[%s] - %s", NAME, CAUSE);
    }
}
