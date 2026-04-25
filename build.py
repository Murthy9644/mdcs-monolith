import os, sys, colorama

colorama.init(autoreset= True)

acknowledge = '''
    python "build.py" <command> <module>

    # works only for this project structure & when run from root

    commands:
        compile = clean compile files (including dependencies) from specified module
        exec    = run Main class from specified module
        build   = clean build .jar modules (of dependencies also) from specified module

    module flags:
        module={module path}
        # uses "cli" as default if not specidied

    example: python "build.py" compile module=cli\n
'''

def codeStrings(command, module):
    string = ''
    classpath = f'apps/{module}/target/classes:core/target/classes:shared/target/classes '

    match command:
        case "compile":
            string = "mvn clean install -DskipTests"

        case "exec":
            string = f"mvn -pl apps/{module} exec:java -Dexec.mainClass={module}.Main"

        case "build":
            string = f"mvn -pl apps/{module} -am clean package"

    return string

class Build:

    def run(self):
        exitcode = os.system(codeStrings(self.command, self.module))

        if exitcode == 0:
            print(colorama.Fore.GREEN + "Job successful: exit status code 0")

        else: 
            print(colorama.Fore.RED + f"Job failed: exit status code {exitcode}")

    def __init__(self):

        try:
            self.command = sys.argv[1]

            if self.command not in ["build", "exec", "compile"]:
                print(colorama.Fore.RED + "unknown command:", self.command)
                print(colorama.Fore.CYAN + acknowledge)
                sys.exit()

            try: self.module = sys.argv[2]

            except IndexError: 
                print(colorama.Fore.LIGHTYELLOW_EX + "module not specified: defaulting to cli")
                self.module = "cli"

        except IndexError:
            print(colorama.Fore.RED + "command not specified: service terminated")
            print(colorama.Fore.CYAN + acknowledge)
            print(colorama.Fore.RESET, end = '')
            sys.exit()

        self.run()

Build()
print(colorama.Fore.RESET, end = '')