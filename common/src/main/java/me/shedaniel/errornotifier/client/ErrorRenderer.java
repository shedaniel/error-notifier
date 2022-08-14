package me.shedaniel.errornotifier.client;

import me.shedaniel.errornotifier.URLUtils;
import me.shedaniel.errornotifier.api.ErrorProvider;
import me.shedaniel.errornotifier.launch.EarlyGraphics;
import me.shedaniel.errornotifier.launch.EarlyWindowRenderer;

import java.io.IOException;
import java.util.List;

public class ErrorRenderer implements EarlyWindowRenderer {
    public final List<ErrorProvider.ErrorComponent> errors;
    public StringEntryListWidget listWidget;
    
    public ErrorRenderer(List<ErrorProvider.ErrorComponent> errors) {
        this.errors = errors;
    }
    
    @Override
    public void render(EarlyGraphics graphics, double mouseX, double mouseY, float tickDelta) {
        listWidget = new StringEntryListWidget(graphics.getScaledWidth(), graphics.getScaledHeight(), 32, graphics.getScaledHeight());
        listWidget.addItem(new EmptyItem());
        listWidget.addItem(new TextItem("Errors:", 0xFFFF5555));
        for (ErrorProvider.ErrorComponent error : errors) {
            for (String s : graphics.splitString("- " + error.message().getMessage(), graphics.getScaledWidth() - 40)) {
                listWidget.addItem(new TextItem(s, -1));
            }
            String url = error.url();
            if (url != null) {
                for (String s : graphics.splitString(url, graphics.getScaledWidth() - 40)) {
                    listWidget.addItem(new LinkItem(s, url));
                }
            }
        }
        listWidget.addItem(new EmptyItem());
        listWidget.addItem(new EmptyItem());
        listWidget.addItem(new TextItem("Minecraft may not be launched in this state!", 0xFF999999));
        listWidget.addItem(new TextItem("Please fix the issues and restart!", 0xFF999999));
        for (StringItem child : listWidget.children()) {
            listWidget.max = Math.max(listWidget.max, child.getWidth(graphics));
        }
        listWidget.render(graphics, (int) mouseX, (int) mouseY, tickDelta);
        graphics.drawStringCenteredWithShadow("Startup Errors", graphics.getScaledWidth() / 2, 13, -1);
    }
    
    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (listWidget != null) {
            listWidget.mouseClicked(mouseX, mouseY, button);
        }
    }
    
    private static class StringEntryListWidget extends DynamicErrorFreeEntryListWidget<StringItem> {
        private boolean inFocus;
        private int max = 80;
        
        public StringEntryListWidget(int width, int height, int startY, int endY) {
            super(width, height, startY, endY, "textures/gui/options_background.png");
        }
        
        public void creditsClearEntries() {
            clearItems();
        }
        
        private StringItem rei_getEntry(int int_1) {
            return this.children().get(int_1);
        }
        
        public void creditsAddEntry(StringItem entry) {
            addItem(entry);
        }
        
        @Override
        public int getItemWidth() {
            return max;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return width - 40;
        }
    }
    
    private abstract static class StringItem extends DynamicErrorFreeEntryListWidget.Entry<StringItem> {
        public abstract int getWidth(EarlyGraphics graphics);
    }
    
    private static class EmptyItem extends StringItem {
        @Override
        public void render(EarlyGraphics matrixStack, int i, int i1, int i2, int i3, int i4, int i5, int i6, boolean b, float v) {
            
        }
        
        @Override
        public int getItemHeight() {
            return 5;
        }
        
        @Override
        public int getWidth(EarlyGraphics graphics) {
            return 0;
        }
    }
    
    private static class TextItem extends StringItem {
        private String text;
        private int color;
        
        public TextItem(String text, int color) {
            this.text = text;
            this.color = color;
        }
        
        @Override
        public void render(EarlyGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            graphics.drawStringWithShadow(text, x + 5, y, color);
        }
        
        @Override
        public int getItemHeight() {
            return 12;
        }
        
        @Override
        public int getWidth(EarlyGraphics graphics) {
            return graphics.width(text) + 10;
        }
    }
    
    private class LinkItem extends StringItem {
        private String text;
        private String link;
        private boolean contains;
        
        public LinkItem(String text, String link) {
            this.text = text;
            this.link = link;
        }
        
        @Override
        public void render(EarlyGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
            contains = mouseX >= x && mouseX <= x + entryWidth && mouseY >= y && mouseY <= y + entryHeight;
            float width = graphics.drawStringWithShadow(text, x + 5, y, 0xff1fc3ff);
            if (contains) {
                graphics.fill(x + 5, y + 8, (int) (x + 5 + width), y + 9, 0xff1fc3ff);
            }
        }
        
        @Override
        public int getItemHeight() {
            return 12;
        }
        
        @Override
        public int getWidth(EarlyGraphics graphics) {
            return graphics.width(text) + 10;
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (contains && button == 0) {
                try {
                    URLUtils.openForked(link);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }
}
