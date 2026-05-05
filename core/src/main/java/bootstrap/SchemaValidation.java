package bootstrap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import file_io.DataClasses;
import file_io.FileIO;
import file_io.DataClasses.FieldRules;

public class SchemaValidation {
    private static HashMap<String, String> response;
    private static HashMap<Class<?>, List<DataClasses.FieldRules>> schema_design = new HashMap<>();

    public static boolean checkSchema(){
        try{
            for (Class<?> data_class : schema_design.keySet()){
                JsonNode node = FileIO.getJsonNode(data_class);

                for (FieldRules rule : schema_design.get(data_class)){
                    JsonNode field = node.get(rule.name);

                    if (field == null) return false;

                    // Work under progress !
                }
            }

            return true;
        } catch (Exception e){ return false; }
    }
    
    public static boolean validate(HashMap<String, String> res)
    throws IOException, IllegalAccessException, NoSuchFieldException{
        response = res;

        List<Class<?>> data_classes = List.of(
            DataClasses.Accounts.class,
            DataClasses.Device.class,
            DataClasses.ModulePaths.class,
            DataClasses.Configs.class,
            DataClasses.Data.class
        );

        for (Class<?> c : data_classes){
            schema_design.putIfAbsent(c, new ArrayList<>());
            Field class_fields[] = c.getDeclaredFields();

            for (Field field : class_fields){

                if (Modifier.isStatic(field.getModifiers())) continue;

                schema_design.get(c).add(
                    new DataClasses.FieldRules(
                        field.getName(),
                        field.getType().getSimpleName().toLowerCase()
                    )
                );
            }
        }

        boolean status = checkSchema();
        schema_design.clear();

        if (!status){
            res.put("app_state", "continue");
            res.put("status", "INVALID_SCHEMA_FORMAT");

            return false;
        }

        return true;
    }
}
