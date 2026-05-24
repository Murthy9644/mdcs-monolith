package security;

import java.io.IOException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import file_io.DataClasses;
import file_io.FileIO;

public class KeyManager {
    
    private static SecretKey generateKey(){
        try{
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);

            return generator.generateKey();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static SecretKey getKey()
    throws IOException{
        String cikey = FileIO.fileRead(DataClasses.Secrets.getCiPath());

        if (cikey != null && !cikey.isEmpty()){
            byte[] decoded_key = Base64.getDecoder().decode(cikey);
            return new SecretKeySpec(decoded_key, 0, decoded_key.length, "AES");
        }

        SecretKey secret_key = generateKey();

        byte[] key_bytes = secret_key.getEncoded();
        String encoded_key = Base64.getEncoder().encodeToString(key_bytes);

        FileIO.fileWrite(DataClasses.Secrets.getCiPath(), encoded_key, "_");

        return secret_key;
    }
}
