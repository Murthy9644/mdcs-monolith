package com.mdcs.core;

/*
 * Architecture note:
 *
 * This class is the single communication layer used by the Core module.
 *
 * The Core never performs console I/O directly and never knows whether it is running standalone
 * or as a child process. It simply sends and receives typed messages through this stream.
 *
 * Standalone mode: Stream <-> Console
 * Child process mode: Core <-> Stream <-> IPC Pipe <-> Manager
 *
 * The Manager interprets message types (LOG, AUTH, UPDATE_APPROVAL, etc.) and decides how to
 * fulfill them. It may delegate to a CLI, GUI, or any other interface, but that decision is
 * completely outside the Core.
 *
 * Therefore, message types represent services/capabilities requested by the Core, not UI actions.
 * The Core only expresses what it needs, while the Manager decides how to satisfy the request.
 *
 * This class is responsible only for serializing/deserializing the protocol, not for implementing
 * any business or UI logic.
 */

import java.io.PrintStream;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

class IPCWriter implements Runnable{
    private BlockingDeque<String> ipc_queue;

    @Override
    public void run(){
        //
    }

    IPCWriter(BlockingDeque<String> ipc_queue){
        this.ipc_queue = ipc_queue;
    }
}

/**
 * Includes methods to read and write into the buffer (IPC pipes / console)
 * 
 * When launched as a child process, writes / reads into / from the IPC pipe. And when launched as
 * an independent process, it writes or reads from console.
 * 
 * Defines different type of output streams which can be parsed by the parent.
 */

public class Stream {
    private Scanner ipipe;
    private PrintStream opipe;
    private BlockingDeque<String> ipc_queue;

    public enum Type{
        LOG,
        AUTH,
        CRITICAL_UPDATE,
        MINOR_UPDATE,
        PLUGIN_UPDATE
    }

    public void write(Type type, String text){
        this.opipe.println("[" + type.toString() + "]" + "[" + text.length() + "]" + text + ";");
    }

    public String read(){ return this.ipipe.nextLine(); }
    
    public Stream(){
        this.ipipe = new Scanner(System.in);
        this.opipe = System.out;
        this.ipc_queue = new LinkedBlockingDeque<>();

        Thread writer = new Thread(new IPCWriter(this.ipc_queue));
        writer.start();
    }
}
