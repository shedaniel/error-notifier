package me.shedaniel.errornotifier.launch;

public interface EarlyWindowRenderer {
    void render(EarlyGraphics graphics, double mouseX, double mouseY, float tickDelta);
    
    void mouseClicked(double mouseX, double mouseY, int button);
}
