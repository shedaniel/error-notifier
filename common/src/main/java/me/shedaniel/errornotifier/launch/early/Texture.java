package me.shedaniel.errornotifier.launch.early;

public interface Texture {
    int getId();
    
    void upload(boolean blur, boolean wrap);
}