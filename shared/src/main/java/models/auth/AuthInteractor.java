package models.auth;

public interface AuthInteractor {
    
    String getUsername();

    String getEmail();

    String getPassword();

    String confirmPassword();

    String getOTP();

    String getDeviceName();

    String getWorkspaceName();
}
