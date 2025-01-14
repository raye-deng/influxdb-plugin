package jenkinsci.plugins.rc.influxdb.generators.serenity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SerenityJsonSummaryFile implements ISerenityJsonSummaryFile {

    private static final String SERENITY_OUTPUT_DIRECTORY = "target/site/serenity";
    private static final String SERENITY_JSON_SUMMARY_FILE = "serenity-summary.json";

    private final String workspace;

    public SerenityJsonSummaryFile(String workspace) {
        this.workspace = workspace;
    }

    public boolean exists() {
        return Files.exists(getPath());
    }

    public Path getPath() {
        return java.nio.file.Paths.get(workspace, SERENITY_OUTPUT_DIRECTORY, SERENITY_JSON_SUMMARY_FILE);
    }

    public String getContents() throws IOException {
        return new String(Files.readAllBytes(getPath()), StandardCharsets.UTF_8);
    }
}