# Prompter

Prompter is a Java library for accepting user input from input streams (e.g. 
from CLI) flexibly and effortlessly. Put the "pro" back in "prompt"!

This is also my student project made for a course at NaUKMA.

## Main features
- Get prompts! Acquire preconfigured prompts from `PromptManager` for a wide 
  selection of 
  simple data types.
- Make prompts! Bind any class you want to a custom-made prompt, 
  making your prompt available through `PromptManager` instead of the default 
  one.
- Promptable objects! Effortlessly prompt custom data aggregate objects 
  marked by the `Promptable` interface, 
  provided they adhere to the JavaBean convention. You can customize 
  messages for every field, and ignore specific fields as well.
- Generation of promptable objects! The library has a package-level annotation 
  `MakePromptable` that lets you specify simple `Promptables` with ease.


## Installation

With Gradle:

```gradle
repositories {
    maven {
        url = "https://maven.pkg.github.com/artynova/prompter"
    }
}

dependencies {
    implementation 'artynova:prompter:1.0-SNAPSHOT'
}

```

## Example usage

```java
import io.github.artynova.prompter.PromptManager;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int number = PromptManager.getPromptFor(int.class).prompt(scanner, System.out, "Please input a number: ");
        System.out.println("You have input " + number);
    }
}

```

## Attributions

The project uses [Checkstyle](https://docs.gradle.org/current/userguide/checkstyle_plugin.html) and [Sonarlint](https://github.com/Lucas3oo/sonarlint-gradle-plugin) Gradle plugins for static code analysis.a

The Checkstyle plugin is configured with a file based on `sun_checks.xml` file 
from the 
official [Checkstyle repository](https://github.com/checkstyle/checkstyle).

## License

[MIT](https://choosealicense.com/licenses/mit/)
