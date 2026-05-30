package cli.helpers;

import java.util.concurrent.BlockingQueue;

import cli.utils.tools.ConsoleIO;
import models.PrintTask;

public class HelperThreads {
    
    public static class PrintToConsole implements Runnable{
        private BlockingQueue<PrintTask> queue;
        private ConsoleIO io;

        private void mapLevel(String level, String line){
            line += "\n";

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
                    this.io.print(line);
                    break;
            }
        }
        
        @Override
        public void run(){
            
            while (true){
                try {
                    PrintTask task = queue.take();
                    String args[] = task.message.split("<>");

                    this.mapLevel(args[0], args[1]);
                    task.latch.countDown();
                } catch (InterruptedException e) {
                    PrintTask task;
                    
                    while ((task = queue.poll()) != null){
                        String args[] = task.message.split("<>");
                        this.mapLevel(args[0], args[1]);
                        task.latch.countDown();
                    }

                    break;
                }
            }
        }

        public PrintToConsole(BlockingQueue<PrintTask> queue, ConsoleIO io){
            this.queue = queue;
            this.io = io;
        }
    }
}
