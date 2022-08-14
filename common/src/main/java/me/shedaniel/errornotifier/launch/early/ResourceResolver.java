package me.shedaniel.errornotifier.launch.early;

import java.io.InputStream;

public interface ResourceResolver {
    InputStream resolve(String url);
}
