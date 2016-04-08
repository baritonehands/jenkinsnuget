package org.jenkinsci.plugins.nuget.publishers;

import java.io.Serializable;

/**
 * @author Arnaud TAMAILLON
 */
public class PublicationResult implements Serializable {
    private final String packageName;
    private boolean success;

    public PublicationResult(String packageName, boolean success) {
        this.packageName = packageName;
        this.success = success;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
