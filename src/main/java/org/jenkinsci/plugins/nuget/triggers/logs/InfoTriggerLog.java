package org.jenkinsci.plugins.nuget.triggers.logs;

import hudson.model.TaskListener;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Arnaud TAMAILLON
 */
public class InfoTriggerLog implements TriggerLog {

    protected final XTriggerLog log;

    public InfoTriggerLog(XTriggerLog log) {
        this.log = log;
    }

    @Override
    public TaskListener getListener() {
        return log.getListener();
    }

    @Override
    public void checkingPackageFile(Path packageFile) {
        log.info(String.format("Checking packages file: %s", packageFile.toAbsolutePath().toString()));
    }

    @Override
    public void packageHasBeenUpdated(String id, String version, String latest) {
        if (latest == null) {
            log.info(String.format("Package %s v%s: no version found.", id, version));
        } else {
            log.info(String.format("Package %s v%s should update to v%s.", id, version, latest));
        }
    }

    @Override
    public void errorWhileParsingPackageConfigFile(SAXException exception) {
        log.error(exception.toString());
    }

    @Override
    public void errorVisitingFile(IOException exception) {
        log.error(exception.toString());
    }

    @Override
    public void skippingFileWithNoFileName() {

    }

    @Override
    public void skippedFileNotPackagesConfig(Path fileName) {

    }

    @Override
    public void packageVersionRetrieved(String id, String latest) {

    }

    @Override
    public void reusingCachedPackageVersion(String id) {

    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void info(String s) {
        log.info(s);
    }
}
