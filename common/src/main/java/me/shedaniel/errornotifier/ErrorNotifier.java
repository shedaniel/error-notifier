package me.shedaniel.errornotifier;

import me.shedaniel.errornotifier.api.ErrorProvider;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;

public class ErrorNotifier {
    public static final String MOD_ID = "error_notifier";
    
    public static void init(String[] args, Path gameDir, @Nullable String mcVersion, boolean client) {
        List<ErrorProvider.ErrorComponent> errorComponents = ServiceLoader.load(ErrorProvider.class).stream()
                .flatMap(provider -> provider.get().errors().stream()).toList();
        if (!errorComponents.isEmpty()) {
            System.err.printf("Found %d error(s) during startup:%n", errorComponents.size());
            errorComponents.forEach(errorComponent -> {
                System.err.printf("- %s%n", errorComponent.message().getMessage());
                
                if (errorComponent.url() != null) {
                    System.err.printf("  - %s%n", errorComponent.url());
                }
            });
            
            if (!client) {
                System.exit(1);
                return;
            }
            
            try {
                Class<?> rendererClass = Class.forName("me.shedaniel.errornotifier.launch.EarlyWindowRenderer");
                Object renderer = rendererClass.cast(Class.forName("me.shedaniel.errornotifier.client.ErrorRenderer")
                        .getDeclaredConstructor(List.class)
                        .newInstance(errorComponents));
                Class.forName("me.shedaniel.errornotifier.launch.EarlyWindow")
                        .getDeclaredMethod("start", String[].class, Path.class, String.class, rendererClass)
                        .invoke(null, args, gameDir, mcVersion, renderer);
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
