package file_io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileIO{
    private static ObjectMapper mapper = new ObjectMapper().enable(INDENT_OUTPUT);

    // Does the file exists?
    public static <F> boolean exists(Class<F> file)
    throws IllegalAccessException, NoSuchFieldException{
        Field path_field = file.getDeclaredField("path");
        Path path = (Path) path_field.get(null);

        return Files.exists(path);
    }

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

    // For writing from generics
    public static <T extends DataClasses.HasPath> void createAndWrite(Class<T> file)
    throws Exception{
        T obj = file.getDeclaredConstructor().newInstance();
        fileWrite(obj);
    }

    // For writing files from objects
    // Required object properties: file.path (expected to exist)
    public static <T extends DataClasses.HasPath> void fileWrite(T file)
    throws Exception{
        String path = file.getPath();
        mapper.writeValue(new File(path), file);
    }

    // General writer (methods: append, write)
    public static void fileWrite(String path, String content, String method)
    throws IOException{

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

    // To get JsonNode
    public static <F> JsonNode getJsonNode(Class<F> file)
    throws NoSuchFieldException, IllegalAccessException, IOException{
        Field path_field = file.getDeclaredField("path");
        String path = (String) path_field.get(null);
        
        return mapper.readTree(new File(path));
    }

    // Get file as class object from JsonNode
    public static <F> F getTreeValue(Class<F> file, JsonNode node)
    throws JsonProcessingException{ return mapper.treeToValue(node, file); }

    // Write to file from ObjectNode
    public static <F> void writeJsonNode(Class<F> file, ObjectNode node)
    throws Exception{
        Field path_field = file.getDeclaredField("path");
        String path = (String) path_field.get(null);

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), node);
    }
}