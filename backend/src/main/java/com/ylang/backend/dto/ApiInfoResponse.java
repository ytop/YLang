package com.ylang.backend.dto;

import java.util.List;

/**
 * Response DTO for API information
 */
public class ApiInfoResponse {
    
    private String name;
    private String version;
    private String description;
    private List<String> availableEndpoints;
    
    public ApiInfoResponse() {}
    
    public ApiInfoResponse(String name, String version, String description, List<String> availableEndpoints) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.availableEndpoints = availableEndpoints;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getAvailableEndpoints() {
        return availableEndpoints;
    }
    
    public void setAvailableEndpoints(List<String> availableEndpoints) {
        this.availableEndpoints = availableEndpoints;
    }
}