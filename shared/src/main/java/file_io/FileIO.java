package file_io;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileIO{
    private static ObjectMapper mapper = new ObjectMapper().enable(INDENT_OUTPUT);

    // For reading files into objects
    // Required object properties: file.path (expected to exist)
    public static <F> F fileRead(Class<F> file) 
    throws IOException, NoSuchFieldException, IllegalAccessException{
        Field path_Field = file.getDeclaredField("path");
        String path = (String) path_Field.get(null);

        return mapper.readValue(new File(path), file);
    }

    // General reader
    public static String fileRead(String path) 
    throws IOException{ return Files.readString(Path.of(path)); }

    // For writing files from objects
    // Required object properties: file.path (expected to exist)
    public static <T extends HasPath> void fileWrite(T file)
    throws Exception{
        String path = file.getPath();
        mapper.writeValue(new File(path), file);
    }

    // General writer (methods: append, write)
    public static void fileWrite(String path, String content, String method)
    throws Exception{

        if (method.equals("append")){
            Files.writeString(
                Path.of(path), 
                content, 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
        } else{
            Files.writeString(Path.of(path), content, StandardOpenOption.CREATE);
        }
    }
}