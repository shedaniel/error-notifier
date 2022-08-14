package me.shedaniel.errornotifier;

import dev.architectury.injectables.annotations.ExpectPlatform;
import me.shedaniel.errornotifier.launch.early.ResourceResolver;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ErrorNotifierPlatform {
    @ExpectPlatform
    public static ResourceResolver getResourceResolver() {
        throw new AssertionError();
    }
    
    @ExpectPlatform
    public static List<Map.Entry<String, Path>> findAllErrorNotifierFiles() {
        throw new AssertionError();
    }
}
