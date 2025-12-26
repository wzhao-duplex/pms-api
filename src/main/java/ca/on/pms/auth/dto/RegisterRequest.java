package ca.on.pms.auth.dto;
public record RegisterRequest(
    String email, 
    String password, 
    String fullName, 
    String orgName
) {}