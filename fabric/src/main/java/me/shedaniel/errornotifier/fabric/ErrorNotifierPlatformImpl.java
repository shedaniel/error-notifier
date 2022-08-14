package me.shedaniel.errornotifier.fabric;

import me.shedaniel.errornotifier.launch.early.ResourceResolver;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ErrorNotifierPlatformImpl {
    public static ResourceResolver getResourceResolver() {
        return url -> {
            Path path = FabricLoader.getInstance().getModContainer("minecraft").get().findPath(url).orElse(null);
            if (path != null && Files.exists(path)) {
                try {
                    return Files.newInputStream(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            InputStream stream = ErrorNotifierPlatformImpl.class.getClassLoader().getResourceAsStream(url);
            if (stream != null) {
                return stream;
            }
            throw new RuntimeException("Resource not found: " + url);
        };
    }
    
    public static List<Map.Entry<String, Path>> findAllErrorNotifierFiles() {
        List<Map.Entry<String, Path>> paths = new ArrayList<>();
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            mod.findPath("error_notifier.json").ifPresent(path -> paths.add(Map.entry(mod.getMetadata().getName() + " " + mod.getMetadata().getVersion().getFriendlyString(), path)));
        }
        return paths;
    }
}
