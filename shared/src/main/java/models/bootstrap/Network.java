package models.bootstrap;

import java.util.List;
import java.util.Map;

import models.network.Http.Response;

public class Network {

    /*
    'Response' class includes common fields for any response from server. We include 'Body' which
    changes per API.
    */

    public static class UpdRes extends Response{

        public static class App{
            public String cur_ver;
            public String avail_ver;
            public boolean critical_update;
        }

        public static class Plugin{
            public String curr_ver;
            public String avail_ver;
            public boolean compatible;
            public boolean upd_req;
        }

        public static class Body{
            public App app;
            public Map<String, Plugin> plugins;
            public List<String> changes;
        }

        public Body body;
    }
}
