package models.auth;

import java.util.List;
import java.util.ArrayList;

public class SignupResponse {

    public enum State{
        SUCCESS,
        FAIL
    }

    // Not to return to AuthPipe, but to write to a file.
    public static class SignupData{
        public int user_id;
        public String username;
        public String email;
        public String auth_token;
        public int device_id;
        public String device_name;
        public int workspace_id;
        public String workspace_name;
    }
    
    // Generic bootstrap issue/report
    public static class AuthIssue {
        public String phase;
        public State status;
        public List<String> issues = new ArrayList<>();
        public String message;
    
        @Override
        public String toString() {
            return "AuthIssue{" +
                    "phase='" + phase + '\'' +
                    ", status=" + status +
                    ", issues=" + issues +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
    
    public static class GeneralResponse{
        State auth_state;
        List<AuthIssue> reports = new ArrayList<>();
        String summary;

        @Override
        public String toString() {
            return "AuthResponse{" +
                    "\n  auth_state=" + auth_state +
                    ",\n  reports=" + reports +
                    ",\n  summary='" + summary + '\'' +
                    "\n}";
        }
    }
}
