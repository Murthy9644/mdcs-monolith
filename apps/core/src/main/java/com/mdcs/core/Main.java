package com.mdcs.core;

/*
Manages core functionalities of the application like, bootstrap, user and device authentication.

Expects IPC to pass the messages to & from the parents (Both way communication required) and
writes logs, raises flags and requests data via those pipes.

The return status is independent of the IPC and launch mechanisms.

If launched by a parent, it will generally receive a pipe to communicate, so the required data
will be available at runtime. But, if launched independently, the IPC pipe will make the process
to sleep and after a timeout the process exits with a status code.
*/

public class Main {
    
    /*
    The execution flow depends on the arguments passed when launching this process.
    It requires subcommand to mention the subroutine
    */
    public static void main(String[] args) {

        // Streams to write/read into/from parent buffer
        Stream stream = new Stream();
    }
}
