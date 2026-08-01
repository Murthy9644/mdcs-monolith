package com.mdcs.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

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
 * 
 * Stream logging structure: "[id][type][length]<payload>"
 */

/**
 * Includes methods to read and write into the buffer (IPC pipes / console)
 * 
 * When launched as a child process, writes / reads into / from the IPC pipe. And when launched
 * as an independent process, it writes or reads from console.
 * 
 * Defines different type of output streams which can be parsed by the parent.
 */

public class Stream {
    private BlockingDeque<Message> oque;
    private ConcurrentHashMap<Integer, CompletableFuture<Response>> promises;
    private AtomicInteger id_count;

    public static class Response{
        private final int id;
        private final String type;
        private final String payload;

        public int getId(){ return this.id; }
        
        public String getPayload(){ return this.payload; }
        
        public String getType(){ return this.type; }

        public Response(String header[], String payload){
            this.id = Integer.parseInt(header[0]);
            this.type = header[1];
            this.payload = payload;
        }
    }

    private static class Message{
        private final String msg;

        String get(){ return this.msg; }

        Message(int id, String type, String payload){
            this.msg = "[" + id + "]"
                + "[" + type.toString() + "]"            
                + "[" + payload.length() + "]"
                + payload;
        }
    }

    private  static class IPCReader implements Runnable{
        private BufferedInputStream istream;
        private ConcurrentHashMap<Integer, CompletableFuture<Response>> promises;

        @Override
        public void run(){
            
            try{
                while (true){
                    String header[] = new String[3];
                    StringBuilder group = new StringBuilder();
                    int brackets = 0;

                    while (brackets < 3){
                        int chr = this.istream.read();

                        if (chr == -1) throw new EOFException();

                        char ch = (char) chr;

                        if (ch == '[') continue;

                        if (ch == ']'){
                            header[brackets++] = group.toString();
                            group.setLength(0);
                        }
                        
                        else group.append(ch);
                    }

                    for (int i = 0; i < Integer.parseInt(header[2]); i++){
                        int chr;

                        if ((chr = this.istream.read()) == -1)
                            throw new EOFException();

                        group.append((char) chr);
                    }

                    Response res = new Response(header, group.toString());
                    CompletableFuture<Response> promise = this.promises.remove(res.getId());

                    if (promise != null) promise.complete(res);
                }
            } catch (IOException e){
                // Will decide what to do here later.
            }
        }

        IPCReader(ConcurrentHashMap<Integer, CompletableFuture<Response>> promises){
            this.istream = new BufferedInputStream(System.in);
            this.promises = promises;
        }
    }

    private static class IPCWriter implements Runnable{
        private BlockingDeque<Message> oque;
        private BufferedOutputStream ostream;

        @Override
        public void run(){

            try{
                while (true){
                    Message msg = oque.take();

                    // write to stdout so parent/manager can read it
                    this.ostream.write(msg.get().getBytes(StandardCharsets.UTF_8));
                    ostream.flush();
                }

            } catch(InterruptedException e){
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                // Will decide what to do here later.
            }
        }

        IPCWriter(BlockingDeque<Message> oque){
            this.oque = oque;
            this.ostream = new BufferedOutputStream(System.out);
        }
    }

    public enum Type{
        AUTH,
        LOG,
        CRITICAL_UPDATE,
        MINOR_UPDATE,
        PLUGIN_UPDATE
    }

    /**
     * This method is best for Fire-And-Forget kind of payloads. Best use cases are Logs, Updates,
     * and Notifications.
     */
    public void send(Type type, String payload)
    throws InterruptedException{
        int id = this.id_count.incrementAndGet();
        this.oque.put(new Message(id, type.toString(), payload));
    }

    /**
     * Returns CompletableFuture for the request. Adds the request made by the sub process to the
     * queue provides promise for the response.
     * 
     * Best for requests which expect a response, eg., Auth.
     */
    public CompletableFuture<Response> request(Type type, String payload)
    throws InterruptedException{
        int id = this.id_count.incrementAndGet();

        CompletableFuture<Response> promise = new CompletableFuture<>();
        this.promises.put(id, promise);

        this.oque.put(new Message(id, type.toString(), payload));

        return promise;
    }
    
    public Stream(){
        this.oque = new LinkedBlockingDeque<>();
        this.promises = new ConcurrentHashMap<>();
        this.id_count = new AtomicInteger();

        Thread writer = new Thread(new IPCWriter(this.oque));
        writer.setDaemon(true);
        writer.start();
        
        Thread reader = new Thread(new IPCReader(this.promises));
        writer.setDaemon(true);
        reader.start();
    }
}