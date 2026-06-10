package cli.utils.tools;

import java.util.Scanner;

public class ConsoleIO{
    private Scanner input;

    public void error(String text){
        String log_level = "[ " + Colors.RED + "ERROR " + Colors.RESET + "]   ";
        System.out.print(log_level + text);
    }

    public void success(String text){
        String log_level = "[ " + Colors.GREEN + "SUCCESS " + Colors.RESET + "] ";
        System.out.print(log_level + text);
    }

    public void warn(String text){
        String log_level = "[ " + Colors.YELLOW + "WARN " + Colors.RESET + "]    ";
        System.out.print(log_level + text);
    }

    public void info(String text){
        String log_level = "[ " + Colors.CYAN + "INFO " + Colors.RESET + "]    ";
        System.out.print(log_level + text);
    }

    public void critical(String text){
        String log_level = "[ " + Colors.BOLD + Colors.RED + "FATAL " + Colors.RESET + "] ";
        System.out.print(log_level + text);
    }

    public void muted(String text){ System.out.print(Colors.GRAY + text + Colors.RESET); }

    public void print(String text){ System.out.print(text); }

    public void highlight(String text){
        System.out.print("\n" + Colors.BG_WHITE + Colors.BOLD + Colors.BLACK + text + Colors.RESET + "\n");
    }

    public void heading(String text){
        System.out.print(Colors.BOLD + Colors.WHITE + text + Colors.RESET);
    }

    public void specifier(String text){
        System.out.print(Colors.BRIGHT_PURPLE + text + Colors.RESET);
    }

    public String ask(){ return input.nextLine(); }

    public void map(String level, String line){
        line += "\n";

        switch (level) {
            case "info":
                this.info(line);
                break;

            case "error":
                this.error(line);
                break;

            case "warn":
                this.warn(line);
                break;

            case "success":
                this.success(line);
                break;

            case "critical":
                this.critical(line);
                break;
            
            default:
                this.print(line);
                break;
        }
    }

    public ConsoleIO(){
        input = new Scanner(System.in);
    }
}