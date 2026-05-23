package models.auth;

public class SignupResponse {

    public enum AuthState{
        SUCCESS,
        FAIL,
        TERMINATE
    }

    public static AuthState setAuthState(AuthState curr, AuthState to){
        // Can only be downgraded (SUCCESS -> FAIL ...)

        if (curr == AuthState.SUCCESS) return to;
        if (
            curr == AuthState.FAIL 
            && (
                to == AuthState.TERMINATE
                || to == AuthState.FAIL
            )
        ) return to;

        if (curr == AuthState.TERMINATE) return curr;

        return to;
    }
}
