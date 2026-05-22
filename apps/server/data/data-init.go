package data

import (
	"encoding/json"
	"mdcs-server/core/models"
	"os"
)

// Load the persistence files into memory, write back to the disk
// when application shutdown - write back to the files

/*
During the server startup, the file contents are cached and operations
are done on them.
Because, frequent read and writes to the disks are inefficient.
*/

type Cache struct {
	Users      models.Users
	Workspaces models.Workspaces
	Devices    models.Devices
}

/*
Note:
Concurrent data access sync is not handled right now.
Assuming low contention access of `Store` for current version for
simplicity

Later versions will implement RWTMutex protection for `Store`
*/
var Store Cache

var files = map[string]any{
	"./data/disk/users.json":      &Store.Users,
	"./data/disk/workspaces.json": &Store.Workspaces,
	"./data/disk/devices.json":    &Store.Devices,
}

/*
Bootstrap ensures file existence, schema validity, format correctness.
Assuming those conditions, can just check for other exceptions
*/

// Caches the data from persistence files
func LoadFiles() error {

	for file, obj := range files {
		data, err := os.ReadFile(file)

		if err != nil {
			return err
		}

		err = json.Unmarshal(data, obj)

		if err != nil {
			return err
		}
	}

	return nil
}

// Flushes cached data to persistence files.
// Intended for graceful shutdown/checkpoint operations.
func FlushFiles() error {

	for file, obj := range files {
		data, err := json.MarshalIndent(obj, "", "    ")

		if err != nil {
			return err
		}

		err = os.WriteFile(file, data, 0644)

		if err != nil {
			return err
		}
	}

	return nil
}
