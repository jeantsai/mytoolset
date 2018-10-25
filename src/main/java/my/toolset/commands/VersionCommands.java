package my.toolset.commands;


import my.toolset.definitions.VersionFileDefinition;
import my.toolset.definitions.VersionFileDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@ShellCommandGroup("Version Management Commands")
@ShellComponent
public class VersionCommands {
    Logger logger = LoggerFactory.getLogger(VersionCommands.class);

    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR_INVALID
            = "[ERROR] Cannot extract version, please check the definition in file 'ver-files.properties'.";
    private static final String ERROR_NOT_FOUND
            = "[ERROR] No version defined in this file, please check the definition in file 'ver-files.properties'.";
    private static final String ERROR_NOT_MATCH
            = "[ERROR] Version does not match the expected one.";


    @Autowired
    private VersionFileDefinitions definitions;


    @ShellMethod(
            key = "change-versions",
            value = "Change the version to a given value in all locations defined in ver-files.properties."
    )
    public void changeVersions(
            @ShellOption(
                    value = {"-t", "--target-version"},
                    help = "The version being changed to."
            )
            @NotBlank
                    String targetVersion,
            @ShellOption(
                    value = {"-c", "--current-version"},
                    help = "The version that all existing ones should be. Otherwise the change will be rejected.",
                    defaultValue = ShellOption.NULL
            )
                    String currentVersion
    ) {
        logger.debug("Total " + definitions.getDefinitions().size() + " files defined.");

        if (ShellOption.NULL.equals(currentVersion)) {
            printNormalString(
                    "\nChange all existing versions in total %s files to %s ...\n\n",
                    Integer.toString(definitions.getDefinitions().size()),
                    targetVersion
            );
        } else if (checkVersions(currentVersion)) {
            printNormalString(
                    "\nChange all existing versions in total %s files from %s to %s ...\n\n",
                    Integer.toString(definitions.getDefinitions().size()),
                    currentVersion,
                    targetVersion
            );
        } else {
            return;
        }

        int count = 0;
        for (VersionFileDefinition d : definitions.getDefinitions()) {
            printNormalString("%s) %-60s", Integer.toString(++count), d.getPath());
            try {
                Path path = Paths.get(d.getPath());
                String content = new String(Files.readAllBytes(path));
                if (!exist(content, d.getRegex())) {
                    printFailureString("\t%s\n", ERROR_NOT_FOUND);
                    continue;
                } else {
                    String result = content.replaceAll(d.getRegex(), String.format(d.getReplacement(), targetVersion));
                    Files.write(path, result.getBytes());
                    printSuccessString("\t[%s]\n", SUCCESS);
                }
//                logger.debug("Result: " + result);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        printLineEnding();
    }

    @ShellMethod(
            key = "check-versions",
            value = "List version numbers in every places specified by ver-files.properties."
    )
    public boolean checkVersions(
            @ShellOption(
                    value = {"-c", "--current-version"},
                    help = "The version that all existing ones should be. Otherwise, an error will be reported.",
                    defaultValue = ShellOption.NULL
            )
                    String currentVersion
    ) {
        logger.debug("Total " + definitions.getDefinitions().size() + " files defined.");

        printNormalString(
                "\nChecking existing versions in total %s files ...\n\n",
                Integer.toString(definitions.getDefinitions().size())
        );
        boolean result = true;
        int count = 0;
        for (VersionFileDefinition d : definitions.getDefinitions()) {
            printNormalString("%s) %-60s", Integer.toString(++count), d.getPath());
            boolean found = true;
            try {
                Path path = Paths.get(d.getPath());
                String content = new String(Files.readAllBytes(path));
                found = checkVersions(content, d.getRegex(), d.getGroupNumber(), currentVersion);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!found) {
                result = false;
            }
        }
        printLineEnding();
        return result;
    }


    private boolean checkVersions(String content, String regex, int groupNumber, String expectedVersion) {
        Matcher matcher = search(content, regex);
        boolean result = true;
        int count = 0;
        while (matcher.find()) {
            count++;
            String actualVersion = null;
            try {
                actualVersion = matcher.group(groupNumber);
                if (StringUtils.hasText(expectedVersion) &&
                        !expectedVersion.equals(actualVersion)) {
                    result = false;
                    printFailureString("\t%s\t%s\n", actualVersion, ERROR_NOT_MATCH);
                } else {
                    printSuccessString("\t%s\n", actualVersion);
                }
            } catch (Exception e) {
                result = false;
                printFailureString("\t%s\n", ERROR_INVALID);
            }
        }
        if (count == 0) {
            result = false;
            printFailureString("\t%s\n", ERROR_NOT_FOUND);
        }

        return result;
    }

    private void printLineEnding() {
        System.out.println();
    }

    private void printNormalString(String format, String... args) {
        System.out.print(String.format(format, args));
    }

    private void printSuccessString(String format, String... args) {
        System.out.print(String.format(
                successString(format),
                args
        ));
    }

    private void printFailureString(String format, String... args) {
        System.out.print(String.format(
                failureString(format),
                args
        ));
    }

    private String successString(String msg) {
        return AnsiOutput.toString(
                AnsiColor.GREEN,
                msg,
                AnsiColor.DEFAULT
        );
    }


    private String failureString(String msg) {
        return AnsiOutput.toString(
                AnsiColor.RED,
                msg,
                AnsiColor.DEFAULT
        );
    }

    private boolean exist(String content, String regex) {
        return search(content, regex).find();
    }

    private Matcher search(String content, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(content);
    }

}
