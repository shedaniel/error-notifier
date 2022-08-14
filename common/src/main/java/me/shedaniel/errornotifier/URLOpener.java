package me.shedaniel.errornotifier;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;

public class URLOpener {
    public static void main(String[] args) throws IOException {
        DataInputStream stream = new DataInputStream(System.in);
        String url = stream.readUTF();
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI.create(url));
        } else {
            Runtime.getRuntime().exec("xdg-open " + url);
        }
    }
}
