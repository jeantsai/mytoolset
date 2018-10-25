package my.toolset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ToolSetApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ToolSetApplication.class)
                .run(args);
    }
}
