package data

import (
	"encoding/json"
	"mdcs-server/models"
	"os"
)

// Load the persistence files into memory, write back to the disk when application
// shutdown - write back to the files

/*
During the server startup, the file contents are cached and operations are done on
them.
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
Assuming low contention access of `Store` for current version for simplicity

Later versions will implement RWTMutex protection for `Store`
*/
var Store Cache

/*
I am writing this here because I don't know where else to write it. This is global
variable and can be used any where. Btw, if you can make it read-only and allow
writing data only by a specific function, perfect!

This was meant to be written in modules/version package as it is used there only
right now, but I didn't know where to write it in there.
*/
var Metadata *models.VerData

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
func Load() error {

	for file, obj := range files {
		data, err := os.ReadFile(file)

		if err != nil {
			// Files doesn't exists
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

/*
What if flush failed?

All the data will be lost and server goes offline. We should
implement some fallback for it. Something like, storing data somewhere else
temporarily and get it back when server was restarted and persist locally. Good news
is, this is just a temporary case, until the database is introduced.
*/
func Flush() error {

	for file, obj := range files {
		data, err := json.MarshalIndent(obj, "", "  ")

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
