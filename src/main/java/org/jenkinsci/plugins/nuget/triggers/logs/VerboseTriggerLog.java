package org.jenkinsci.plugins.nuget.triggers.logs;

import org.jenkinsci.lib.xtrigger.XTriggerLog;

import java.nio.file.Path;

/**
 * @author Arnaud TAMAILLON
 */
public class VerboseTriggerLog extends InfoTriggerLog {

    public VerboseTriggerLog(XTriggerLog log) {
        super(log);
    }

    @Override
    public void skippingFileWithNoFileName() {
        log.info("Skipped file (no file name can be retrieved)");
    }

    @Override
    public void skippedFileNotPackagesConfig(Path fileName) {
        log.info(String.format("Skipped file (not packages.config): %s", fileName.toAbsolutePath().toString()));
    }

    @Override
    public void packageVersionRetrieved(String id, String latest) {
        if (latest == null) {
            log.info(String.format("Latest version for Package %s: no version found.", id));
        } else {
            log.info(String.format("Latest version for Package %s is v%s.", id, latest));        }

    }

    @Override
    public void reusingCachedPackageVersion(String id) {
        log.info(String.format("Reusing cached version for Package %s.", id));
    }
}
