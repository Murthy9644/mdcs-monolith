package cli.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config{
    public Properties property;
    
    public Config(String filename) throws IOException{
        
        try (
            InputStream reader = Config.class
                .getClassLoader()
                .getResourceAsStream(filename)
        ){
            property = new Properties();            

            if (reader == null) throw new IOException();

            property.load(reader);
        }
    }
}