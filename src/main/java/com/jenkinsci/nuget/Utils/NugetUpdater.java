/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jenkinsci.nuget.Utils;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.jenkinsci.lib.xtrigger.XTriggerLog;

/**
 *
 * @author bgregg
 */
public class NugetUpdater {
    private FilePath solutionDir;
    private XTriggerLog log;
    
    public NugetUpdater(FilePath solutionDir, XTriggerLog log) {
        this.solutionDir = solutionDir;
        this.log = log;
        
    }
    
    public boolean performUpdate() {
        //String solutionGlob = "glob:" + file.getAbsolutePath() + "\\*.sln";
        try {
            makeWritable();
        } catch (Exception ex) {
            log.error(ex.toString());
            return false;
        }
        return false;
    }
    
    private void makeWritable() throws InterruptedException, IOException {
        solutionDir.act(new FileCallable<Void>() {
            public Void invoke(File file, VirtualChannel vc) throws IOException, InterruptedException {
                String packagesGlob = "glob:**\\packages.config";
                
                final PathMatcher matcher = FileSystems.getDefault().getPathMatcher(packagesGlob);
                Files.walkFileTree(Paths.get(file.getPath()), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (matcher.matches(file)) {
                            log.info(String.format("Marking writable: %s", file.toAbsolutePath().toString()));
                            File f = new File(file.toString());
                            boolean success = f.setWritable(true);
                            log.info(String.format("Writable success: %b, %s", success, file.toAbsolutePath().toString()));
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        log.error(exc.toString());
                        return FileVisitResult.CONTINUE;
                    }
                });
                return null;
            }
        });
        
    }
}
