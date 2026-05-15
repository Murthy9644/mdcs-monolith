package cli.utils.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader{
    public Properties property;
    
    public ConfigLoader(String filename) throws IOException{
        
        try (
            InputStream reader = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream(filename)
        ){
            property = new Properties();            

            if (reader == null) throw new IOException();

            property.load(reader);
        }
    }
}