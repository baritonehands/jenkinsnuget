package org.jenkinsci.plugins.nuget.utils;

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.taskdefs.Input;

import java.io.*;
import java.util.List;

/**
 * @author Arnaud TAMAILLON
 */
class ForkingOutputStream extends OutputStream {
    private OutputStream stream;
    private ByteArrayOutputStream forkedStream = new ByteArrayOutputStream();

    public ForkingOutputStream(OutputStream stream) {
        super();
        this.stream = stream;
    }

    @Override
    public void write(int b) throws IOException {
        stream.write(b);
        forkedStream.write(b);
    }

    public List<String> getLines() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(forkedStream.toByteArray());
        List<String> result = IOUtils.readLines(is);
        is.close();
        forkedStream.close();
        return result;
    }
}
