package my.toolset.commands;


import my.toolset.definitions.VersionFileDefinition;
import my.toolset.definitions.VersionFileDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@ShellComponent
public class SetCommand {
    Logger logger = LoggerFactory.getLogger(SetCommand.class);


    @Autowired
    private VersionFileDefinitions definitions;


    @ShellMethod("Set a given version to all locations defined in setver.yml in batch.")
    public void set(
            @ShellOption({"-T --to"})
            @Size(min = 1, max = 40)
                    String ver) {
        logger.info("Total " + definitions.getDefinitions().size() + " files defined.");

        for (VersionFileDefinition d : definitions.getDefinitions()) {
            logger.debug("Definition: {path: " + d.getPath()
                    + ", regex: " + d.getRegex()
                    + "}");
            String content = "";
            String result = null;
            try {
                Path path = Paths.get(d.getPath());
                content = new String(Files.readAllBytes(path));
                exist(content, d.getRegex());
                result = content.replaceFirst(d.getRegex(), String.format(d.getReplacement(), ver));
//                logger.debug("Result: " + result);
                Files.write(path, result.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean exist(String content, String regex) {

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        int count = 0;
        while (matcher.find()) {
            count++;
            logger.debug("Found " + count + " : " + matcher.start() + " - " + matcher.end());
        }
        return count > 0;
    }
}
