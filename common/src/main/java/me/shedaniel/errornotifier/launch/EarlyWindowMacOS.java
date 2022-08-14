package me.shedaniel.errornotifier.launch;

import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import org.lwjgl.glfw.GLFWNativeCocoa;

import java.util.Optional;

public class EarlyWindowMacOS {
    public static void toggleMacOSFullscreen(long l) {
        getNsWindow(l).filter(EarlyWindowMacOS::isInMacOSKioskMode).ifPresent(EarlyWindowMacOS::toggleMacOSFullscreen);
    }
    
    private static boolean isInMacOSKioskMode(NSObject nSObject) {
        return ((Long) nSObject.sendRaw("styleMask", new Object[0]) & 16384L) == 16384L;
    }
    
    private static void toggleMacOSFullscreen(NSObject nSObject) {
        nSObject.send("toggleFullScreen:");
    }
    
    private static Optional<NSObject> getNsWindow(long l) {
        long m = GLFWNativeCocoa.glfwGetCocoaWindow(l);
        return m != 0L ? Optional.of(new NSObject(new Pointer(m))) : Optional.empty();
    }
}
