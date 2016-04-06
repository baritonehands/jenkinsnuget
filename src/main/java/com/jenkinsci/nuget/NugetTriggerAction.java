package com.jenkinsci.nuget;

import hudson.Util;
import hudson.console.AnnotatedLargeText;
import hudson.model.Action;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import hudson.model.BuildableItem;
import org.apache.commons.jelly.XMLOutput;

/**
 *
 * @author bgregg
 */
public class NugetTriggerAction implements Action {
    private transient BuildableItem job;
    private transient File logFile;
    
    public NugetTriggerAction(BuildableItem job, File logFile) {
        this.job = job;
        this.logFile = logFile;
    }
    
    @SuppressWarnings("unused")
    public BuildableItem getOwner() {
        return job;
    }
    
    public String getIconFileName() {
        return "clipboard.gif";
    }

    public String getDisplayName() {
        return "Nuget Trigger Log";
    }

    public String getUrlName() {
        return "nugettriggerPollLog";
    }
    
    @SuppressWarnings("unused")
    public String getLog() throws IOException {
        return Util.loadFile(getLogFile());
    }

    public File getLogFile() {
        return logFile;
    }

    @SuppressWarnings("unused")
    public void writeLogTo(XMLOutput out) throws IOException {
        new AnnotatedLargeText<>(getLogFile(), Charset.defaultCharset(), true, this).writeHtmlTo(0, out.asWriter());
    }
}
