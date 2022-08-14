package me.shedaniel.errornotifier.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface ErrorProvider {
    List<ErrorComponent> errors();
    
    interface MessageComponent {
        String getMessage();
        
        static MessageComponent of(String message) {
            return () -> message;
        }
        
        static MessageComponent of(String message, Object... args) {
            return () -> String.format(message, args);
        }
    }
    
    record ErrorComponent(MessageComponent message, @Nullable String url) {
        public ErrorComponent(MessageComponent message) {
            this(message, null);
        }
        
        public ErrorComponent withUrl(@Nullable String url) {
            return new ErrorComponent(message, url);
        }
    }
}
