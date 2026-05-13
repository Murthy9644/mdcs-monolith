package response_classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BootstrapResponse{

    // Enums for app state
    public static enum AppState{
        TERMINATE,
        BLOCK,
        CONTINUE
    }

    // Enums for status
    public static enum Status{
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
    public static enum UserState{
        USER_AUTH_REQUIRED,
        USER_LOGGED_IN
    }

    // Enums for update type
    public static enum UpdateType{
        CRITICAL,
        OPTIONAL,
        PATCH
    }

    // Helper class - To hold plugin info
    public static class PluginInfo{
        public boolean is_compatible;
        public String installed_ver;
        public String available_ver;
    }

    // Helper class - To hold update related info
    public static class UpdateInfo{
        
        // App updates
        public boolean app_update_avail;
        public UpdateType update_type;
        public String app_curr_ver;
        public String app_avail_ver;

        // Plugin updates
        public Map<String, PluginInfo> plugin_ver = new HashMap<>(); // Plugin name -> [current ver, available ver]

        public List<String> changes = new ArrayList<>();
    }

    // Main response class
    public static class GeneralResponse{
        public AppState app_state;
        public UserState user_state;
        public Status status;
        public List<String> body = new ArrayList<>();
        public String message;
        public UpdateInfo update_info;
    }
}
