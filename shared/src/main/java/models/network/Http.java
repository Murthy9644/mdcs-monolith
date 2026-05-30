package models.network;

import java.util.HashMap;
import java.util.Map;

// Contains DTOs for HTTP requests and responses

public class Http {

    public enum Method{
        GET,
        POST
    }
    
    /*
    These DTOs are meant to be extended. These provide basic members that are common in any
    transaction, and remaining members/methods are to be included in the child.
    */

    /*
    Suggestion is, create a class for a particular api which extends these abstract classes and 
    'Body' DTO in the respective package in `shared/models`.
    */
   
    public static abstract class Request{
        public Method method;
        public String endpoint;
        public Map<String, String> headers = new HashMap<>();
    }

    public static abstract class Response{
        public boolean status;
        public String error;
        public String message;
    }
}
