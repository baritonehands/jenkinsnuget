package org.jenkinsci.plugins.nuget.utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import hudson.FilePath;
import org.jenkinsci.plugins.nuget.NugetGlobalConfiguration;
import org.jenkinsci.plugins.nuget.triggers.logs.TriggerLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

/**
 * @author Arnaud TAMAILLON
 */
class NugetPackageCheckerVisitor extends SimpleFileVisitor<Path> {

    private final Map<String, String> latestPackageVersions = Maps.newHashMap();
    private final TriggerLog log;
    private final boolean preReleaseChecked;
    private final FilePath workspaceRoot;
    private final DocumentBuilder builder;
    private final NugetGlobalConfiguration configuration;
    private boolean updated;

    boolean isUpdated() {
        return updated;
    }

    NugetPackageCheckerVisitor(TriggerLog log, NugetGlobalConfiguration configuration, boolean preReleaseChecked, FilePath workspaceRoot) throws ParserConfigurationException {
        this.log = log;
        this.configuration = configuration;
        this.preReleaseChecked = preReleaseChecked;
        this.workspaceRoot = workspaceRoot;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        builder = factory.newDocumentBuilder();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
        Path fileName = file.getFileName();
        if (fileName == null) {
            log.skippingFileWithNoFileName();
            return FileVisitResult.CONTINUE;
        }
        if (!fileName.toString().equalsIgnoreCase("packages.config")) {
            log.skippedFileNotPackagesConfig(file);
            return FileVisitResult.CONTINUE;
        }
        log.checkingPackageFile(file);
        return checkPackageFile(file);
    }

    private FileVisitResult checkPackageFile(Path file) throws IOException {
        try {
            Document doc = builder.parse(file.toFile());

            doc.getDocumentElement().normalize();
            NodeList packageNodes = doc.getElementsByTagName("package");
            for (int idx = 0; idx < packageNodes.getLength(); idx++) {
                Element p = (Element) packageNodes.item(idx);
                String id = p.getAttribute("id");
                String version = p.getAttribute("version");
                String latest = getPackageVersion(workspaceRoot, id);
                log.packageVersionRetrieved(id, latest);

                if (latest == null || !version.equals(latest)) {
                    log.packageHasBeenUpdated(id, version, latest);
                    updated = true;
                    return FileVisitResult.TERMINATE;
                }
            }
        } catch (SAXException ex) {
            log.errorWhileParsingPackageConfigFile(ex);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        log.errorVisitingFile(exc);
        return FileVisitResult.CONTINUE;
    }

    private String getPackageVersion(FilePath workspaceRoot, String packageName) throws IOException {
        if (latestPackageVersions.containsKey(packageName)) {
            log.reusingCachedPackageVersion(packageName);
            return latestPackageVersions.get(packageName);
        }
        NugetGetLatestPackageVersionCommand command = new NugetGetLatestPackageVersionCommand(log, configuration, workspaceRoot, packageName, preReleaseChecked);
        command.execute();
        return command.getVersion();
    }

    @VisibleForTesting
    public Map<String, String> getLatestPackageVersions() {
        return latestPackageVersions;
    }
}
