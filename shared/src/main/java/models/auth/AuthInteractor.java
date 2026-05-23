package models.auth;

public interface AuthInteractor {
    
    String getUsername();

    String getEmail();

    void pswdRules();

    String getPassword();

    String confirmPassword();

    void pswdsMismatch();

    String getOTP();

    String getDeviceName();

    String getWorkspaceName();
}
