package resources;

import base.Resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationResource extends Resource {
    private static final Pattern PATH_PATTERN = Pattern.compile("//[^/]+(/.*)$");
    public String gameWebSocketUrl;

    public String getGameWebSocketPath() {
        Matcher matcher = ConfigurationResource.PATH_PATTERN.matcher(this.gameWebSocketUrl);
        matcher.find();
        return matcher.group(1);
    }
}
