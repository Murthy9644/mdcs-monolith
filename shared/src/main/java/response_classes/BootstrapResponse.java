package response_classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BootstrapResponse {

    // Enums for app state
    public static enum AppState {
        TERMINATE,
        BLOCK,
        CONTINUE
    }

    // Enums for status
    public static enum Status {
        CHECK,
        INVALID_VERSION_FORMAT,
        UPDATE_CHECK_FAILED,
        INVALID_FILE_FORMAT,
        INVALID_FILE_SCHEMA,
        FILE_WRITE_FAILURE,
        CRITICAL_UPDATE,
        INVALID_UPDATE_RESPONSE
    }

    // Enums for user state
    public static enum UserState {
        USER_AUTH_REQUIRED,
        USER_LOGGED_IN
    }

    // Enums for update type
    public static enum UpdateType {
        CRITICAL,
        OPTIONAL,
        PATCH
    }

    // Generic bootstrap issue/report
    public static class BootstrapIssue {
        public String phase;
        public Status status;
        public List<String> issues = new ArrayList<>();
        public String message;

        @Override
        public String toString() {
            return "BootstrapIssue{" +
                    "phase='" + phase + '\'' +
                    ", status=" + status +
                    ", issues=" + issues +
                    ", message='" + message + '\'' +
                    '}';
        }
    }

    // Helper class - To hold plugin info
    public static class PluginInfo {
        public boolean is_compatible;
        public String installed_ver;
        public String available_ver;

        @Override
        public String toString() {
            return "PluginInfo{" +
                    "is_compatible=" + is_compatible +
                    ", installed_ver='" + installed_ver + '\'' +
                    ", available_ver='" + available_ver + '\'' +
                    '}';
        }
    }

    // Helper class - To hold update related info
    public static class UpdateInfo {
        public Status status;
        public String message;

        // App updates
        public boolean app_update_avail;
        public UpdateType update_type;
        public String app_curr_ver;
        public String app_avail_ver;

        // Plugin updates : plugin_name -> plugin_ver
        public Map<String, PluginInfo> plugin_ver = new HashMap<>();

        public List<String> changes = new ArrayList<>();

        @Override
        public String toString() {
            return "UpdateInfo{" +
                    "\n    status=" + status +
                    ",\n    message='" + message + '\'' +
                    ",\n    app_update_avail=" + app_update_avail +
                    ",\n    update_type=" + update_type +
                    ",\n    app_curr_ver='" + app_curr_ver + '\'' +
                    ",\n    app_avail_ver='" + app_avail_ver + '\'' +
                    ",\n    plugin_ver=" + plugin_ver +
                    ",\n    changes=" + changes +
                    "\n  }";
        }
    }

    // Main response class
    public static class GeneralResponse {
        public AppState app_state;
        public UserState user_state;

        // Unified bootstrap reports
        public List<BootstrapIssue> reports = new ArrayList<>();

        public UpdateInfo update_info = new UpdateInfo();
        public String summary;

        public void setAppState(AppState state) {
            // Can only be stepped up
            // CONTINUE -> BLOCK -> TERMINATE

            if (this.app_state == AppState.TERMINATE)
                return;

            if (
                this.app_state == AppState.BLOCK
                && state == AppState.CONTINUE
            )
                return;

            this.app_state = state;
        }

        @Override
        public String toString() {
            return "BootstrapResponse{" +
                    "\n  app_state=" + app_state +
                    ",\n  user_state=" + user_state +
                    ",\n  reports=" + reports +
                    ",\n  update_info=" + update_info +
                    ",\n  summary='" + summary + '\'' +
                    "\n}";
        }
    }
}
