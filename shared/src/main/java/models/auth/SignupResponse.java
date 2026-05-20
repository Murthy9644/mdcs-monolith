package models.auth;

public class SignupResponse {

    public enum AuthState{
        SUCCESS,
        FAIL
    }

    public static AuthState setAuthState(AuthState curr, AuthState to){
        // Can only be downgraded (SUCCESS -> FAIL ...)

        if (curr == AuthState.SUCCESS) return to;
        if (curr == AuthState.FAIL) return curr;

        return to;
    }
}
