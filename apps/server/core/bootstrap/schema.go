package bootstrap

// Manages data persistence in local JSON files
// Used for v1 where data is stored locally on the server

import (
	"encoding/json"
	"fmt"
	"mdcs-server/core/models"
	"os"
)

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
	"users":      "data/disk/users.json",
	"workspaces": "data/disk/workspaces.json",
	"devices":    "data/disk/devices.json",
}

var defvals = map[string]any{
	"users":      models.Users{},
	"workspaces": models.Workspaces{},
	"devices":    models.Devices{},
}

func ensureSchemaFiles() (bool, error) {
	err := os.MkdirAll("./data/disk", 0755)

	if err != nil {
		return false, err
	}

	for file, path := range files {
		_, err := os.Stat(path)

		if err != nil {

			if os.IsNotExist(err) {
				data, err := json.MarshalIndent(defvals[file], "", "    ")

				if err != nil {
					return false, err
				}

				err = os.WriteFile(path, data, 0644)

				if err != nil {
					return false, err
				}
			} else {
				return false, err
			}
		}
	}

	return true, nil
}

/*
json.Unmarshal handles both invalid json format and schema errors
Missing fields are defaulted to Golang datatype defaults and extra unknown
fields are ignored.
*/
func ensureSchema() (bool, error) {

	for file, path := range files {
		data, err := os.ReadFile(path)

		if err != nil {
			return false, err
		}

		var obj any

		switch file {
		case "users":
			obj = defvals[file].(models.Users)

		case "workspaces":
			obj = defvals[file].(models.Workspaces)

		case "devices":
			obj = defvals[file].(models.Devices)
		}

		err = json.Unmarshal(data, &obj)
		if err != nil {
			return false, err
		}
	}

	return true, nil
}

func SchForValidation() bool {
	_, err := ensureSchemaFiles()

	if err != nil {
		fmt.Println("Required files can't be located: ", err)
		return false
	}

	_, err = ensureSchema()

	if err != nil {
		fmt.Println("File format is invalid: ", err)
		return false
	}

	return true
}
