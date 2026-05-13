package response_classes;

import java.util.List;
import java.util.Map;

public class ServerResponseClasses {

    // Helper for update-check response class
    public static class App{
        public String current_version;
        public String available_version;
        public boolean critical_update;
    }

    // Helper for update-check response class
    public class Plugin {
        public String installed_version;
        public String available_version;
        public boolean is_compatible;
        public boolean update_required;
    }
    
    // For update-check response
    public static class UpdateResponse{
        public App app;
        public Map<String, Plugin> plugins;
        public List<String> changes;
    }
}
