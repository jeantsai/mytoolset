package my.toolset.definitions;

public class VersionFileDefinition {

    private String path;
    private String regex;
    private String replacement = "$1%s$3";
    private int groupNumber = 2;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    @Override
    public String toString() {
        return "VersionFileDefinition{" +
                "path='" + path + '\'' +
                ", regex='" + regex + '\'' +
                ", replacement='" + replacement + '\'' +
                ", groupNumber=" + groupNumber +
                '}';
    }
}
