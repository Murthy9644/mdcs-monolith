# Compile / Build / Execute Commands (MDCS Monolith)

> Run all commands from the project root: `mdcs-monolith/`

---

## Clean Build

- `mvn clean`
> Deletes all previously generated build outputs (`target/` folders) across all modules. Does not compile anything. Useful when builds start behaving unexpectedly.

---

- `mvn clean compile`
> Removes old build outputs and compiles all modules (.java → .class). Compiled files are generated inside each module’s `target/classes` directory.

---

- `mvn clean install`
> Full production-style build. Cleans everything, compiles all modules, runs tests, packages JARs, and installs them into the local Maven repository (`~/.m2`). Ensures all modules are built in correct dependency order.

---

## Compile Only

- `mvn compile`
> Compiles all modules without running tests or packaging. Outputs `.class` files inside each module’s `target/classes` folder.

---

- `mvn -pl "module-name" -am compile`
> Compiles only a specific module and all its dependencies.  
> Example:
> `mvn -pl apps/cli -am compile`  
> Useful for fast development cycles in large projects.

---

## Testing

- `mvn test`
> Compiles source and runs all unit tests across all modules. Does not package or install artifacts.

---

- `mvn -pl "module-name" test`
> Runs tests only for a specific module and its required dependencies.

---

## Packaging

- `mvn package`
> Compiles code and creates JAR files for each module inside their `target/` folders. Does not install them to `.m2`.

---

- `mvn clean package`
> Clean build + compile + test + JAR creation for all modules.

---

## Execution (Running the Application)

### CLI Module Execution

- `mvn -q exec:java -pl apps/cli -Dexec.mainClass="cli.Main"`
> Runs the CLI application directly using Maven without manually handling classpaths.

---

- `mvn clean compile -q exec:java -pl apps/cli -Dexec.mainClass="cli.Main"`
> Clean build + compile + run CLI in one command.

---

## Module-Specific Build Control

- `mvn clean install -pl "module-name"`
> Builds only a specific module and installs it to `.m2`.

---

- `mvn clean install -pl "module-name" -am`
> Builds the module + all its dependencies from source.

---

## Debug / Validation

- `mvn dependency:tree`
> Shows full dependency graph of all modules. Useful for debugging circular or missing dependencies.

---

- `mvn help:effective-pom`
> Shows the fully resolved POM after inheritance and dependency resolution.

---

## Fast Development Cycle (Most Important)

- `mvn -pl apps/cli -am compile`
> Fast compile cycle for active development on CLI module.

---

- `mvn -pl apps/cli -am exec:java -Dexec.mainClass="com.mdcs.cli.Main"`
> Fast build + run cycle for CLI without full project rebuild.

---

## Golden Rule

- Always run commands from:
> `mdcs-monolith/`

- Never compile submodules independently unless debugging.

- Maven automatically handles build order:

```
shared → core → cli
```

---