package bootstrap;

import java.io.IOException;

import file_io.DataClasses;
import file_io.FileIO;
import logger.Log;
import response_classes.BootstrapResponse;

public class UserStateResolution {

    private static DataClasses.Accounts valid(Log logger)
    throws IllegalAccessException, NoSuchFieldException{
        
        try{
            DataClasses.Accounts acc = FileIO.fileRead(DataClasses.Accounts.class);
            logger.info("bootstrap", "File validation successful: Accounts.json");
            
            return acc;
        } catch (IOException e){
            logger.error("bootstrap", "Failed to load file: Accounts.json");

            return null;
        }
    }

    private static BootstrapResponse.UserState userState(DataClasses.Accounts accounts, Log logger){
        BootstrapResponse.UserState state;

        if (accounts == null) state = BootstrapResponse.UserState.USER_AUTH_REQUIRED;
        
        else if (!accounts.login_status) state = BootstrapResponse.UserState.USER_AUTH_REQUIRED;

        else if (
            accounts.user_id == 0
            || (accounts.user_name == null || accounts.user_name.isEmpty())
            || (accounts.email == null || accounts.email.isEmpty())
            || (accounts.auth_token == null || accounts.auth_token.isEmpty())
        ) state = BootstrapResponse.UserState.USER_AUTH_REQUIRED;

        else state = BootstrapResponse.UserState.USER_LOGGED_IN;

        logger.info("bootstrap", "User state resolved to: " + state);

        return state;
    }

    public static BootstrapResponse.UserState resolve(Log logger)
    throws IllegalAccessException, NoSuchFieldException{
        logger.info("bootstrap", "User state resolution started");

        if (!FileIO.exists(DataClasses.Accounts.class)){
            // File deleted or couldn't write file uring initial bootstrap phases
            logger.info("bootstrap", "Failed to find Accounts.json file");

            return BootstrapResponse.UserState.USER_AUTH_REQUIRED;
        }

        return userState(valid(logger), logger);
    }
}
