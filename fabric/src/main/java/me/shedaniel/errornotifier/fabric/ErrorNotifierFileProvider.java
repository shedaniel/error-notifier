package me.shedaniel.errornotifier.fabric;

import com.google.common.base.MoreObjects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.shedaniel.errornotifier.ErrorNotifierPlatform;
import me.shedaniel.errornotifier.api.ErrorProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ErrorNotifierFileProvider implements ErrorProvider {
    @Override
    public List<ErrorComponent> errors() {
        List<ErrorComponent> errors = new ArrayList<>();
        List<Map.Entry<String, Path>> files = ErrorNotifierPlatform.findAllErrorNotifierFiles();
        Gson gson = new GsonBuilder().setLenient().create();
        for (Map.Entry<String, Path> entry : files) {
            Path file = entry.getValue();
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonElement element = gson.fromJson(reader, JsonElement.class);
                if (!element.isJsonObject())
                    throw new IllegalStateException("Invalid error file: %s! Expected JSON object, got: %s".formatted(file, element.toString()));
                JsonObject object = element.getAsJsonObject();
                if (!object.has("schemaVersion"))
                    throw new IllegalStateException("Invalid error file: %s! Expected schemaVersion, got: %s".formatted(file, object.toString()));
                int schemaVersion = object.getAsJsonPrimitive("schemaVersion").getAsInt();
                if (schemaVersion != 1)
                    throw new IllegalStateException("Invalid error file: %s! Expected schemaVersion 1, got: %s".formatted(file, schemaVersion));
                Format format = gson.fromJson(object, Format.class);
                for (Check check : format.checks()) {
                    Object versions = check.versions();
                    if (versions instanceof String) versions = List.of(versions);
                    else if (!(versions instanceof Collection<?>))
                        throw new IllegalStateException("Invalid error file: %s! Expected versions to be a string or collection, got: %s".formatted(file, versions.getClass()));
                    if (check.modId() == null)
                        throw new IllegalStateException("Invalid error file: %s! Expected modId, got: %s".formatted(file, check.modId()));
                    
                    Collection<VersionPredicate> predicates = VersionPredicate.parse((Collection<String>) versions);
                    Optional<ModContainer> containerOptional = FabricLoader.getInstance().getModContainer(check.modId());
                    switch (check.type()) {
                        case "depends":
                            if (containerOptional.isEmpty()) {
                                errors.add(missingDependency(check.modId(), MoreObjects.firstNonNull(check.modName(), check.modId()), entry.getKey(), check.url()));
                            } else {
                                boolean found = false;
                                for (VersionPredicate predicate : predicates) {
                                    if (predicate.test(containerOptional.get().getMetadata().getVersion())) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    errors.add(wrongVersion(check.modId(), MoreObjects.firstNonNull(check.modName(), containerOptional.get().getMetadata().getName()) + " " + containerOptional.get().getMetadata().getVersion().getFriendlyString(),
                                            entry.getKey(), (Collection<String>) versions, check.url()));
                                }
                            }
                            break;
                        case "breaks":
                            if (containerOptional.isPresent()) {
                                boolean found = false;
                                for (VersionPredicate predicate : predicates) {
                                    if (predicate.test(containerOptional.get().getMetadata().getVersion())) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (found) {
                                    errors.add(breaksVersion(check.modId(), MoreObjects.firstNonNull(check.modName(), containerOptional.get().getMetadata().getName()) + " " + containerOptional.get().getMetadata().getVersion().getFriendlyString(),
                                            entry.getKey(), (Collection<String>) versions, check.url()));
                                }
                            }
                            break;
                        default:
                            throw new IllegalStateException("Invalid error file: %s! Unknown check type: %s".formatted(file, check.type()));
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return errors;
    }
    
    private ErrorComponent missingDependency(String id, String name, String requestingMod, String url) {
        return new ErrorComponent(MessageComponent.of("%s is a required dependency of %s but it is not installed!".formatted(name, requestingMod)), url);
    }
    
    private ErrorComponent wrongVersion(String id, String name, String requestingMod, Collection<String> versions, String url) {
        StringBuilder versionString = new StringBuilder();
        int i = 0;
        for (String version : versions) {
            if (i == versions.size() - 1 && versions.size() > 1) {
                versionString.append(" or ");
            } else if (i != 0) {
                versionString.append(", ");
            }
            versionString.append('\'').append(version).append('\'');
            i++;
        }
        
        return new ErrorComponent(MessageComponent.of("%s is an unsupported version of %s! Please use %s!".formatted(name, requestingMod, versionString.toString())), url);
    }
    
    private ErrorComponent breaksVersion(String id, String name, String requestingMod, Collection<String> versions, String url) {
        StringBuilder versionString = new StringBuilder();
        int i = 0;
        for (String version : versions) {
            if (i == versions.size() - 1 && versions.size() > 1) {
                versionString.append(" and ");
            } else if (i != 0) {
                versionString.append(", ");
            }
            versionString.append('\'').append(version).append('\'');
            i++;
        }
        
        return new ErrorComponent(MessageComponent.of("%s is an unsupported version of %s! Please avoid %s!".formatted(name, requestingMod, versionString.toString())), url);
    }
    
    private static final class Format {
        private List<Check> checks;
        
        public List<Check> checks() {
            return checks;
        }
    }
    
    private static final class Check {
        private String type;
        private String modId;
        private @Nullable String modName;
        private Object versions;
        private @Nullable String url;
        
        public String type() {
            return type;
        }
        
        public String modId() {
            return modId;
        }
        
        @Nullable
        public String modName() {
            return modName;
        }
        
        public Object versions() {
            return versions;
        }
        
        @Nullable
        public String url() {
            return url;
        }
    }
}
