package com.mdcs.core;

/*
New implementation of Report (core -> manager)

This implementation has some tweaks on the previous Report and Envelop mechanisms.

    - Logs:
    previously: Log module
    changed to: Stream (to the manager)

    - App state
    previously: AppState module
    changed to: exit codes

    - Completely removed JobType and Summary. Manager doesn't need to know master - worker
    architecture of Core.

Stream logging structure: [type][length]<payload>;

Point to be noted is, this stream is just a communication between Core and Manager, and the logs
are not for UI purpose but mainly for internal use. UI module will decide what/how to show.
*/

class App {

    /*
    There is no fnctionaity to write the logs in this module, the logs are written into stream of
    the manager, which then writes them.
    */

    private Stream stream;
    
    App(Stream stream){
        this.stream = stream;
    }
}
