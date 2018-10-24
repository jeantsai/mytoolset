package my.toolset.definitions;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@PropertySources({
        @PropertySource("classpath:ver-files.properties"),
        @PropertySource(value="file:ver-files.properties", ignoreResourceNotFound=true)
})
@ConfigurationProperties(prefix="version-files")
public class VersionFileDefinitions {

    private List<VersionFileDefinition> definitions = new ArrayList<>();

    public List<VersionFileDefinition> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<VersionFileDefinition> definitions) {
        this.definitions = definitions;
    }
}
