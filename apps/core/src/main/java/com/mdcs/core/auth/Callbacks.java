package com.mdcs.core.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Callbacks {
    private final String username;
    private final String email;
    private final String pswd;
    private final String device_name;
    private final String workspace_name;

    public String username(){ return this.username; }
    public String email(){ return this.email; }
    public String pswd(){ return this.pswd; }
    public String deviceName(){ return this.device_name; }
    public String workspaceName(){ return this.workspace_name; }

    @JsonCreator
    public Callbacks(
        @JsonProperty("username") String username,
        @JsonProperty("email") String email,
        @JsonProperty("pswd") String pswd, 
        @JsonProperty("device_name") String device_name, 
        @JsonProperty("workspace_name") String workspace_name
    ){
        this.username = username;
        this.email = email;
        this.pswd = pswd;
        this.device_name = device_name;
        this.workspace_name = workspace_name;
    }
}
