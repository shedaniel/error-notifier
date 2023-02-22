package me.shedaniel.errornotifier;

import me.shedaniel.errornotifier.api.ErrorProvider;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

public class ErrorNotifier {
    public static final String MOD_ID = "error_notifier";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init(String[] args, Path gameDir, @Nullable String mcVersion, boolean client) {
        List<ErrorProvider.ErrorComponent> errorComponents = ServiceLoader.load(ErrorProvider.class).stream()
                .flatMap(provider -> provider.get().errors().stream()).toList();
        if (!errorComponents.isEmpty()) {
            LOGGER.printf(Level.ERROR, "Found %d error(s) during startup:", errorComponents.size());
            errorComponents.forEach(errorComponent -> {
                LOGGER.printf(Level.ERROR, "- %s", errorComponent.message().getMessage());
                
                if (errorComponent.url() != null) {
                    LOGGER.printf(Level.ERROR, "%s", errorComponent.url());
                }
            });
            
            if (!client) {
                System.exit(1);
                return;
            }
            
            try {
                if (isMac()) {
                    LOGGER.info("Opening error notifier on Mac...");
                    ForkingUtils.openErrors("Minecraft* " + mcVersion, errorComponents);
                } else {
                    Class<?> rendererClass = Class.forName("me.shedaniel.errornotifier.launch.EarlyWindowRenderer");
                    Object renderer = rendererClass.cast(Class.forName("me.shedaniel.errornotifier.client.ErrorRenderer")
                            .getDeclaredConstructor(List.class)
                            .newInstance(errorComponents));
                    Class.forName("me.shedaniel.errornotifier.launch.EarlyWindow")
                            .getDeclaredMethod("start", String[].class, Path.class, String.class, rendererClass)
                            .invoke(null, args, gameDir, mcVersion, renderer);
                }
                System.exit(1);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static boolean isMac() {
        String string = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        return string.contains("mac");
    }
}
