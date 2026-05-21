package bootstrap

import (
    "os"
    "models" // need to be resolved
    "encoding/json"
)

// Generate and maintain the files in database.
// Used for v1 where data is stored locally on the server

/*
During schema phase of bootstrap,
phase 1: check schema existence
Check if the required files already exist or not

phase 2: file creation
If the required files are missing, create default files

phase 3: schema and format validation
- If invalid data items are present, update with default for that field only
- If the file format (JSON in this case) is incorrect, replace with a default
file
*/

var files = map[string]string{
    "users": "disk/users.json",
    "workspaces": "disk/workspaces.json",
    "devices": "disk/devices.json"
}

var defvals = map[string]any{
    "users": models.Users{},
    "workspaces": models.Workspaces{},
    "devices": models.Devices{}
}

func ensureSchemaFiles() bool{

    for file, path := range files{
        _, err := os.Stat(path)

        if err != nil{
            
            if os.IsNotExist(err){
                data, err := json.Marshal(defvals[file])

                if err != nil{
                    return false
                }

                err = os.WriteFile(path, data, 0644)

                if err != nil{
                    return false
                }
            } else {
                return false
            }
        }
    }

    return true
}

func ensureSchema() bool{
    //
}

func SchForValidation() bool{
    //
}
