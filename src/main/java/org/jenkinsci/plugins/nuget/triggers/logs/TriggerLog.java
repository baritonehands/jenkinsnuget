package org.jenkinsci.plugins.nuget.triggers.logs;

import hudson.model.TaskListener;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * @author Arnaud TAMAILLON
 */
public interface TriggerLog extends Serializable {

    TaskListener getListener();

    void checkingPackageFile(Path packageFile);
    void packageHasBeenUpdated(String id, String version, String latest);

    void errorWhileParsingPackageConfigFile(SAXException exception);
    void errorVisitingFile(IOException exc);

    void skippingFileWithNoFileName();
    void skippedFileNotPackagesConfig(Path fileName);

    void packageVersionRetrieved(String id, String latest);
    void reusingCachedPackageVersion(String id);

    void error(String s);
    void info(String s);
}

