package cli.cli_utils;

import java.util.Scanner;

public class ConsoleIO{
    private Scanner input;

    public void say(String type, String text){
        String print_text;
        
        switch (type) {
            case "primary":
                print_text = Colors.WHITE + text + Colors.RESET;
                break;

            case "success":
                print_text = Colors.GREEN + text + Colors.RESET;
                break;

            case "error":
                print_text = Colors.RED + text + Colors.RESET;
                break;

            case "warning":
                print_text = Colors.YELLOW + text + Colors.RESET;
                break;

            case "muted":
                print_text = Colors.GRAY + text + Colors.RESET;
                break;

            case "highlight":
                print_text = Colors.CYAN + text + Colors.RESET;
                break;

            case "heading":
                print_text = Colors.BOLD + Colors.WHITE + text + Colors.RESET;
                break;

            case "specifier":
                print_text = Colors.BRIGHT_PURPLE + text + Colors.RESET;
                break;
        
            default:
                print_text = text;
                break;
        }

        System.out.print(print_text);
    }

    public String ask(String type, String prompt){
        say(type, prompt);
        return input.nextLine();
    }

    public ConsoleIO(){
        input = new Scanner(System.in);
    }
}