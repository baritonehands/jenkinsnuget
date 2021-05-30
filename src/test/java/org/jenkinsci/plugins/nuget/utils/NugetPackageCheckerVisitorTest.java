package org.jenkinsci.plugins.nuget.utils;

import org.jenkinsci.plugins.nuget.NugetGlobalConfiguration;
import org.jenkinsci.plugins.nuget.triggers.logs.TriggerLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.xml.sax.SAXParseException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class NugetPackageCheckerVisitorTest {

    NugetPackageCheckerVisitor visitor;
    TriggerLog log;

    @Before
    public void setUp() throws Exception {
        log = mock(TriggerLog.class);
        NugetGlobalConfiguration configuration = mock(NugetGlobalConfiguration.class);
        visitor = new NugetPackageCheckerVisitor(
            log,
            configuration,
            true,
            null
        );
        visitor.getLatestPackageVersions().put("Test", "1.0.0");
    }

    @Test
    public void shouldNotBeVulnerableToXxe() throws URISyntaxException, IOException {
        Path file = getFile("xxe");
        FileVisitResult fileVisitResult = visitor.visitFile(file, null);

        ArgumentCaptor<SAXParseException> exceptionArgumentCaptor = ArgumentCaptor.forClass(SAXParseException.class);
        verify(log).errorWhileParsingPackageConfigFile(exceptionArgumentCaptor.capture());
        SAXParseException exception = exceptionArgumentCaptor.getValue();
        assertEquals(DOCTYPE_FORBIDDEN_ERROR, exception.getMessage());
    }

    private Path getFile(String path) throws URISyntaxException {
        URL url = getClass()
            .getClassLoader()
            .getResource("NugetPackageCheckerVisitorTest/" + path + "/packages.config");
        File file = new File(url.toURI());
        return file.toPath();
    }

    final String DOCTYPE_FORBIDDEN_ERROR =
        "DOCTYPE is disallowed when the feature \"http://apache.org/xml/features/disallow-doctype-decl\" set to true.";
}
