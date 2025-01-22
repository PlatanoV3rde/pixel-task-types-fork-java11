package dev.spaxter.pixeltasktypes.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Resources {
    public static String readAsString(InputStream resource) {
        String result = new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
        return result;
    }
}
