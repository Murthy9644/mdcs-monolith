package bootstrap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import file_io.DataClasses;
import file_io.FileIO;
import logger.Log;
import response_classes.BootstrapResponse.*;

public class SchemaValidation {
    private static Log logger;

    public static <T extends DataClasses.HasPath> boolean attemptRecovery(Class<T> file){

        return false;
    }

    public static boolean checkSchema(
        HashMap<Class<? extends DataClasses.HasPath>, List<DataClasses.FieldRules>> schema_design,
        GeneralResponse bsres
    )
    throws Exception{
        boolean schema_validity = true;
        logger.info("bootstrap", "Checking file schemas and format");
        
        BootstrapIssue issue = new BootstrapIssue();
        issue.phase = "Schema Validation";
        issue.status = null;

        for (Class<? extends DataClasses.HasPath> data_class : schema_design.keySet()){
            boolean invalid_schema = false;
            ObjectNode node;

            try{
                JsonNode raw = FileIO.getJsonNode(data_class);
                
                if (raw == null || !raw.isObject()) throw new IOException();

                node = (ObjectNode) raw;

                for (DataClasses.FieldRules rule : schema_design.get(data_class)){
                    JsonNode field = node.get(rule.name);

                    switch (rule.type) {
                        case "int":
                            if (field == null || field.isNull() || !field.isInt()){
                                invalid_schema = true;
                                node.put(rule.name, 0);
                            }
                            break;

                        case "string":
                            if (field == null || field.isNull() || !field.isTextual()){
                                invalid_schema = true;
                                node.put(rule.name, "");
                            }
                            break;

                        case "boolean":
                            if (field == null || field.isNull() || !field.isBoolean()){
                                invalid_schema = true;
                                node.put(rule.name, false);
                            }
                            break;
                    }
                }
            } catch (IOException e){ // Format is invalid (couldn't load file)
                logger.error("bootstrap", "Invalid file format: " + data_class.getSimpleName());
                issue.issues.add("Invalid file format: " + data_class.getSimpleName());

                // Attempt backup (will be added later)

                if (!attemptRecovery(data_class)){ // Placeholder for backup logic
                    // Create defaults
                    logger.error("bootstrap", "Failed to restore backup file: " + data_class.getSimpleName());

                    try{
                        FileIO.createAndWrite(data_class);

                        bsres.setAppState(AppState.CONTINUE);
                        issue.status = Status.INVALID_FILE_FORMAT;
                        issue.message = "Default file created";

                        logger.info("bootstrap", "Created default file: " + data_class.getSimpleName());
                    } catch (Exception e1){
                        bsres.setAppState(AppState.TERMINATE);
                        issue.status = Status.INVALID_FILE_FORMAT;
                        issue.message = "Application startup aborted after recovery attempt failed";

                        logger.error("bootstrap", "Failed to create file: " + data_class.getSimpleName());

                        return false;
                    }
                } else{
                    logger.info("bootstrap", "Backup file restored: " + data_class.getSimpleName());

                    bsres.setAppState(AppState.CONTINUE);
                    issue.status = Status.INVALID_FILE_FORMAT;
                    issue.message = "Backup file restored";
                }

                schema_validity = false; // Acknowledge failure
                node = null;
            }

            if (invalid_schema){
                logger.error("bootstrap", "Invalid file schema: " + data_class.getSimpleName());

                bsres.setAppState(AppState.CONTINUE);
                issue.status = Status.INVALID_FILE_SCHEMA;
                issue.issues.add("Invalid file schema: " + data_class.getSimpleName());

                try{
                    FileIO.writeJsonNode(data_class, node);
                    issue.message = "Defaulted invalid data";

                    logger.info("bootstrap", "Defaulted invalid data: " + data_class.getSimpleName());
                } catch (Exception e) {
                    bsres.setAppState(AppState.TERMINATE);
                    issue.status = Status.FILE_WRITE_FAILURE;
                    issue.issues.add(data_class.getSimpleName());
                    issue.message = "Application startup aborted";

                    logger.error("bootstrap", "Failed to write file: " + data_class.getSimpleName());
                }

                schema_validity = false;
            }
        }

        if (issue.status != null) bsres.reports.add(issue);
        logger.info("bootstrap", "Schemas and format validation passed");

        return schema_validity;
    }
    
    public static boolean validate(GeneralResponse bsres, Log logger_inc)
    throws Exception{
        logger = logger_inc;
        HashMap<Class<? extends DataClasses.HasPath>, List<DataClasses.FieldRules>> schema_design = new HashMap<>();

        List<Class<? extends DataClasses.HasPath>> data_classes = List.of(
            DataClasses.Accounts.class,
            DataClasses.Device.class,
            DataClasses.ModulePaths.class,
            DataClasses.Configs.class,
            DataClasses.Data.class,
            DataClasses.Plugins.class
        );

        for (Class<? extends DataClasses.HasPath> c : data_classes){
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

        // Make sure directories exist already
        FileIO.createAppFileDirs();

        boolean status = checkSchema(schema_design, bsres);
        schema_design.clear();

        return status;
    }
}
