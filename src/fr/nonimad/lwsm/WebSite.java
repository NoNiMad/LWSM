package fr.nonimad.lwsm;

import java.util.Properties;

public class WebSite extends Properties {
    private static final long serialVersionUID = 1L;
    
    public WebSite() {
        super();
        this.setState(WebState.CREATE);
        this.setIfActivated(true);
        this.setServerName("");
        this.setPreviousServerName("");
        this.setDocumentRoot("");
        /*this.setUsingAdvancedConfig(false);
        this.setIfDisplayErrors(true);
        this.setIfLogErrors(true);
        this.setMaxUploadSize("");
        this.setMaxExecutionTime("120");
        this.setUsingCustomLogFile(false);
        this.setErrorLogFile("");*/
    }
    
    // Setters //
    
    public void setState(WebState v) {
        this.setProperty("state", v.toString());
    }
    
    public void setIfActivated(boolean v) {
        this.setProperty("activated", String.valueOf(v));
    }
    
    public void setServerName(String v) {
        this.setProperty("ServerName", v);
    }
    
    public void setPreviousServerName(String v) {
        this.setProperty("OldServerName", v);
    }
    
    public void setDocumentRoot(String v) {
        this.setProperty("DocumentRoot", v);
    }
    
    /*public void setUsingAdvancedConfig(boolean v) {
        this.setProperty("use_adv_config", String.valueOf(v));
    }
    
    public void setIfDisplayErrors(boolean v) {
        if(v)
            this.setProperty("display_errors", "On");
        else
            this.setProperty("display_errors", "Off");
    }
    
    public void setIfLogErrors(boolean v) {
        if(v)
            this.setProperty("log_errors", "On");
        else
            this.setProperty("log_errors", "Off");
    }
    
    public void setMaxUploadSize(String v) {
        this.setProperty("max_upload_size", v);
    }
    
    public void setMaxExecutionTime(String v) {
        this.setProperty("max_execution_time", v);
    }
    
    public void setUsingCustomLogFile(boolean v) {
        this.setProperty("use_custom_log", String.valueOf(v));
    }
    
    public void setErrorLogFile(String v) {
        this.setProperty("error_log", v);
    }*/
    
    // Getters //
    
    public WebState getState() {
        return WebState.valueOf(this.getProperty("state"));
    }
    
    public boolean getIfActivated() {
        return this.getProperty("activated").equals("true");
    }
    
    public String getServerName() {
        return this.getProperty("ServerName");
    }
    
    public String getPreviousServerName() {
        return this.getProperty("OldServerName");
    }
    
    public String getDocumentRoot() {
        return this.getProperty("DocumentRoot");
    }
    
    /*public boolean getUsingAdvancedConfig() {
        return this.getProperty("use_adv_config").equals("true");
    }
    
    public boolean getIfDisplayErrors() {
        return this.getProperty("display_errors").equals("On");
    }
    
    public boolean getIfLogErrors() {
        return this.getProperty("log_errors").equals("On");
    }
    
    public String getMaxUploadSize() {
        return this.getProperty("max_upload_size");
    }
    
    public String getMaxExecutionTime() {
        return this.getProperty("max_execution_time");
    }
    
    public boolean getUsingCustomLogFile() {
        return this.getProperty("use_custom_log").equals("true");
    }
    
    public String getErrorLogFile() {
        return this.getProperty("error_log");
    }*/
}
