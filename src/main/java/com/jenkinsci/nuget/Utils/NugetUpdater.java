/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jenkinsci.nuget.Utils;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jenkinsci.lib.xtrigger.XTriggerLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author bgregg
 */
public class NugetUpdater {
    private FilePath solutionDir;
    private Updater updater;
    private XTriggerLog log;

    public NugetUpdater(FilePath solutionDir, String nugetExe, XTriggerLog log) {
        this.solutionDir = solutionDir;
        this.log = log;
        this.updater = new Updater(nugetExe, log);
    }

    public boolean performUpdate() {
        //String solutionGlob = "glob:" + file.getAbsolutePath() + "\\*.sln";
        try {
            return checkVersions();
        } catch (Throwable ex) {
            log.error(ex.toString());
            return false;
        }
    }

    private boolean checkVersions() throws InterruptedException, IOException {
        return solutionDir.act(updater);
    }   
}

class Updater implements FileCallable<Boolean> {
    private String nugetExe;
    private XTriggerLog log;
    private Map<String, String> packages = new HashMap<String, String>();
    private static final int retryCount = 3;
    
    public Updater(String nugetExe, XTriggerLog log)
    {
        this.log = log;
        this.nugetExe = nugetExe == null ? ".nuget\\NuGet.exe" : nugetExe;
    }

    public Boolean invoke(File file, VirtualChannel vc) throws IOException, InterruptedException {
        final boolean[] updated = new boolean[] { false };
        try {
            final String root = file.getAbsolutePath();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = dbFactory.newDocumentBuilder();

            Files.walkFileTree(Paths.get(file.getPath()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().equalsIgnoreCase("packages.config")) {
                        log.info(String.format("Checking packages file: %s", file.toAbsolutePath().toString()));
                        try {
                            Document doc = builder.parse(file.toFile());

                            doc.getDocumentElement().normalize();
                            NodeList elems = doc.getElementsByTagName("package");
                            for(int idx = 0; idx < elems.getLength(); idx++) {
                                Element p = (Element)elems.item(idx);
                                String id = p.getAttribute("id");
                                String version = p.getAttribute("version");
                                String latest = getPackageVersion(root, id);

                                if(latest == null || !version.equals(latest)) {
                                    log.info(String.format("Package %s v%s should update to v%s.", id, version, latest));
                                    updated[0] = true;
                                    return FileVisitResult.TERMINATE;
                                }
                            }
                        } catch (SAXException ex) {
                            log.error(ex.toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    log.error(exc.toString());
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (ParserConfigurationException ex) {
            log.error(ex.toString());
        }
        return Boolean.valueOf(updated[0]);
    }

    private String getPackageVersion(String wsRoot, String id) throws IOException {
        String line;        

        if(packages.containsKey(id)) {
            return packages.get(id);
        }

        String nuget = new File(nugetExe).isAbsolute() ? nugetExe : new File(wsRoot, nugetExe).getAbsolutePath();

        String cmd = String.format("\"%s\" list %s -NonInteractive", nuget, id);
        for (int retried = 0; retried < retryCount; retried++) {    
            log.info(String.format("Running: %s", cmd));
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = stdout.readLine()) != null) {
                log.info(line);
                String[] parts = line.split(" ", 2);
                if(parts.length == 2 && parts[0].equalsIgnoreCase(id)) {
                    packages.put(id, parts[1]);
                    return parts[1];
                }
            }
            stdout.close();
            while ((line = stderr.readLine()) != null) {
                log.error(line);
            }
            stderr.close();
            try {
                p.waitFor();
                break;
            } catch (InterruptedException ex) {
                log.error(ex.toString());
                log.info(String.format("Retrying: %i", retried));
            }
        }
        return null;
    }
}
