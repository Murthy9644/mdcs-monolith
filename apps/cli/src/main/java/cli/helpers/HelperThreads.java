package cli.helpers;

import java.util.concurrent.BlockingQueue;

import cli.utils.tools.ConsoleIO;

public class HelperThreads {
    
    public static class PrintToConsole implements Runnable{
        private BlockingQueue<String> queue;
        private ConsoleIO io;

        private void mapLevel(String level, String line){

            switch (level) {
                case "info":
                    this.io.info(line);
                    break;

                case "error":
                    this.io.error(line);
                    break;

                case "warn":
                    this.io.warn(line);
                    break;

                case "success":
                    this.io.success(line);
                    break;

                case "critical":
                    this.io.critical(line);
                    break;
            
                default:
                    this.io.info(line);
                    break;
            }
        }
        
        public void run(){
            
            while (true){
                try {
                    String line = queue.take();
                    String args[] = line.split("<>");

                    this.mapLevel(args[0], args[1]);
                } catch (InterruptedException e) {
                    // Exit thread
                    return;
                }
            }
        }

        public PrintToConsole(BlockingQueue<String> queue, ConsoleIO io){
            this.queue = queue;
            this.io = io;
        }
    }
}
