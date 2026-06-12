package models.auth;

public interface Provider {
    
    String username();

    String email();

    void pswdRules();

    String pswd();

    String confirmPswd();

    void pswdsMismatch();

    String otp();

    String deviceName();

    String workspaceName();
}
