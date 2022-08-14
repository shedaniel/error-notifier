package me.shedaniel.errornotifier.client;

import com.google.common.collect.Lists;
import me.shedaniel.errornotifier.launch.ColorUtil;
import me.shedaniel.errornotifier.launch.EarlyGraphics;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public abstract class DynamicErrorFreeEntryListWidget<E extends DynamicErrorFreeEntryListWidget.Entry<E>> {
    protected static final int DRAG_OUTSIDE = -2;
    private final List<E> entries = new Entries();
    public int width;
    public int height;
    public int top;
    public int bottom;
    public int right;
    public int left;
    protected boolean verticallyCenter = true;
    protected int yDrag = -2;
    protected double scroll;
    protected boolean scrolling;
    @Nullable
    protected E hoveredItem;
    protected String backgroundLocation;
    
    public DynamicErrorFreeEntryListWidget(int width, int height, int top, int bottom, String backgroundLocation) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
        this.backgroundLocation = backgroundLocation;
    }
    
    public int getItemWidth() {
        return 220;
    }
    
    public final List<E> children() {
        return this.entries;
    }
    
    protected final void clearItems() {
        this.entries.clear();
    }
    
    protected E getItem(int index) {
        return this.children().get(index);
    }
    
    protected int addItem(E item) {
        this.entries.add(item);
        return this.entries.size() - 1;
    }
    
    protected int getItemCount() {
        return this.children().size();
    }
    
    protected final E getItemAtPosition(double mouseX, double mouseY) {
        int listMiddleX = this.left + this.width / 2;
        int minX = listMiddleX - this.getItemWidth() / 2;
        int maxX = listMiddleX + this.getItemWidth() / 2;
        int currentY = (int) (Math.floor(mouseY - (double) this.top) + (int) this.getScroll() - 4);
        int itemY = 0;
        int itemIndex = -1;
        for (int i = 0; i < entries.size(); i++) {
            E item = getItem(i);
            itemY += item.getItemHeight();
            if (itemY > currentY) {
                itemIndex = i;
                break;
            }
        }
        return mouseX < (double) this.getScrollbarPosition() && mouseX >= minX && mouseX <= maxX && itemIndex >= 0 && currentY >= 0 && itemIndex < this.getItemCount() ? this.children().get(itemIndex) : null;
    }
    
    public void updateSize(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.top = top;
        this.bottom = bottom;
        this.left = 0;
        this.right = width;
    }
    
    public void setLeftPos(int left) {
        this.left = left;
        this.right = left + this.width;
    }
    
    protected int getMaxScrollPosition() {
        List<Integer> list = new ArrayList<>();
        int i = 0;
        for (E entry : entries) {
            i += entry.getItemHeight();
            if (entry.getMorePossibleHeight() >= 0) {
                list.add(i + entry.getMorePossibleHeight());
            }
        }
        list.add(i);
        return list.stream().max(Integer::compare).orElse(0);
    }
    
    protected void clickedHeader(int int_1, int int_2) {
    }
    
    protected void drawBackground() {
    }
    
    @Deprecated
    protected void renderBackBackground(EarlyGraphics graphics) {
        graphics.bindTexture(backgroundLocation);
        graphics.innerBlit(left, right, top, bottom, 0, this.left / 32.0F, this.right / 32.0F,
                ((this.top + (int) this.getScroll()) / 32.0F), ((this.bottom + (int) this.getScroll()) / 32.0F),
                255 << 24 | 32 << 16 | 32 << 8 | 32);
    }
    
    public void render(EarlyGraphics graphics, int mouseX, int mouseY, float delta) {
        this.drawBackground();
        int scrollbarPosition = this.getScrollbarPosition();
        int int_4 = scrollbarPosition + 6;
        renderBackBackground(graphics);
        int rowLeft = this.getRowLeft();
        int startY = this.top + 4 - (int) this.getScroll();
        this.renderList(graphics, rowLeft, startY, mouseX, mouseY, delta);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        this.renderHoleBackground(graphics, 0, this.top, 255);
        this.renderHoleBackground(graphics, this.bottom, this.height, 255);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        graphics.fillGradient(left, top, right, top + 4, 0xFF000000, 0);
        graphics.fillGradient(left, bottom, right, bottom - 4, 0xFF000000, 0);
        int maxScroll = this.getMaxScroll();
        renderScrollBar(graphics, maxScroll, scrollbarPosition, int_4);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    protected void renderScrollBar(EarlyGraphics graphicsint, int maxScroll, int scrollbarPositionMinX, int scrollbarPositionMaxX) {
        if (maxScroll > 0) {
            int int_9 = ((this.bottom - this.top) * (this.bottom - this.top)) / this.getMaxScrollPosition();
            int_9 = clamp(int_9, 32, this.bottom - this.top - 8);
            int int_10 = (int) this.getScroll() * (this.bottom - this.top - int_9) / maxScroll + this.top;
            if (int_10 < this.top) {
                int_10 = this.top;
            }
            
            // TODO
//            RenderSystem.setShader(GameRenderer::getPositionColorShader);
//            Matrix4f matrix = matrices.last().pose();
//            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
//            buffer.vertex(matrix, scrollbarPositionMinX, this.bottom, 0.0F).color(0, 0, 0, 255).endVertex();
//            buffer.vertex(matrix, scrollbarPositionMaxX, this.bottom, 0.0F).color(0, 0, 0, 255).endVertex();
//            buffer.vertex(matrix, scrollbarPositionMaxX, this.top, 0.0F).color(0, 0, 0, 255).endVertex();
//            buffer.vertex(matrix, scrollbarPositionMinX, this.top, 0.0F).color(0, 0, 0, 255).endVertex();
//            buffer.vertex(matrix, scrollbarPositionMinX, int_10 + int_9, 0.0F).color(128, 128, 128, 255).endVertex();
//            buffer.vertex(matrix, scrollbarPositionMaxX, int_10 + int_9, 0.0F).color(128, 128, 128, 255).endVertex();
//            buffer.vertex(matrix, scrollbarPositionMaxX, int_10, 0.0F).color(128, 128, 128, 255).endVertex();
//            buffer.vertex(matrix, scrollbarPositionMinX, int_10, 0.0F).color(128, 128, 128, 255).endVertex();
//            buffer.vertex(scrollbarPositionMinX, (int_10 + int_9 - 1), 0.0F).color(192, 192, 192, 255).endVertex();
//            buffer.vertex((scrollbarPositionMaxX - 1), (int_10 + int_9 - 1), 0.0F).color(192, 192, 192, 255).endVertex();
//            buffer.vertex((scrollbarPositionMaxX - 1), int_10, 0.0F).color(192, 192, 192, 255).endVertex();
//            buffer.vertex(scrollbarPositionMinX, int_10, 0.0F).color(192, 192, 192, 255).endVertex();
//            tessellator.end();
        }
    }
    
    protected void centerScrollOn(E item) {
        double d = (this.bottom - this.top) / -2d;
        for (int i = 0; i < this.children().indexOf(item) && i < this.getItemCount(); i++)
            d += getItem(i).getItemHeight();
        this.capYPosition(d);
    }
    
    protected void ensureVisible(E item) {
        int rowTop = this.getRowTop(this.children().indexOf(item));
        int int_2 = rowTop - this.top - 4 - item.getItemHeight();
        if (int_2 < 0)
            this.scroll(int_2);
        int int_3 = this.bottom - rowTop - item.getItemHeight() * 2;
        if (int_3 < 0)
            this.scroll(-int_3);
    }
    
    protected void scroll(int int_1) {
        this.capYPosition(this.getScroll() + (double) int_1);
        this.yDrag = -2;
    }
    
    public double getScroll() {
        return this.scroll;
    }
    
    public void capYPosition(double double_1) {
        this.scroll = clamp(double_1, 0.0F, this.getMaxScroll());
    }
    
    protected int getMaxScroll() {
        return Math.max(0, this.getMaxScrollPosition() - (this.bottom - this.top - 4));
    }
    
    public int getScrollBottom() {
        return (int) this.getScroll() - this.height;
    }
    
    protected void updateScrollingState(double double_1, double double_2, int int_1) {
        this.scrolling = int_1 == 0 && double_1 >= (double) this.getScrollbarPosition() && double_1 < (double) (this.getScrollbarPosition() + 6);
    }
    
    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }
    
    public boolean mouseClicked(double double_1, double double_2, int int_1) {
        this.updateScrollingState(double_1, double_2, int_1);
        if (!this.isMouseOver(double_1, double_2)) {
            return false;
        } else {
            E item = this.getItemAtPosition(double_1, double_2);
            if (item != null) {
                if (item.mouseClicked(double_1, double_2, int_1)) {
                    return true;
                }
            } else if (int_1 == 0) {
                this.clickedHeader((int) (double_1 - (double) (this.left + this.width / 2 - this.getItemWidth() / 2)), (int) (double_2 - (double) this.top) + (int) this.getScroll() - 4);
                return true;
            }
            
            return this.scrolling;
        }
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && this.scrolling) {
            if (mouseY < (double) this.top) {
                this.capYPosition(0.0F);
            } else if (mouseY > (double) this.bottom) {
                this.capYPosition(this.getMaxScroll());
            } else {
                double double_5 = Math.max(1, this.getMaxScroll());
                int int_2 = this.bottom - this.top;
                int int_3 = clamp((int) ((float) (int_2 * int_2) / (float) this.getMaxScrollPosition()), 32, int_2 - 8);
                double double_6 = Math.max(1.0D, double_5 / (double) (int_2 - int_3));
                this.capYPosition(this.getScroll() + deltaY * double_6);
            }
            
            return true;
        } else {
            return false;
        }
    }
    
    public boolean mouseScrolled(double double_1, double double_2, double double_3) {
        this.capYPosition(this.getScroll() - double_3 * (double) (getMaxScroll() / getItemCount()) / 2.0D);
        return true;
    }
    
    public static int clamp(int i, int j, int k) {
        if (i < j) {
            return j;
        } else {
            return i > k ? k : i;
        }
    }
    
    public static long clamp(long l, long m, long n) {
        if (l < m) {
            return m;
        } else {
            return l > n ? n : l;
        }
    }
    
    public static float clamp(float f, float g, float h) {
        if (f < g) {
            return g;
        } else {
            return f > h ? h : f;
        }
    }
    
    public static double clamp(double d, double e, double f) {
        if (d < e) {
            return e;
        } else {
            return d > f ? f : d;
        }
    }
    
    public boolean isMouseOver(double double_1, double double_2) {
        return double_2 >= (double) this.top && double_2 <= (double) this.bottom && double_1 >= (double) this.left && double_1 <= (double) this.right;
    }
    
    protected void renderList(EarlyGraphics graphics, int startX, int startY, int int_3, int int_4, float float_1) {
        this.hoveredItem = this.isMouseOver(int_3, int_4) ? this.getItemAtPosition(int_3, int_4) : null;
        int itemCount = this.getItemCount();
        for (int renderIndex = 0; renderIndex < itemCount; ++renderIndex) {
            E item = this.getItem(renderIndex);
            int itemY = startY;
            for (int i = 0; i < entries.size() && i < renderIndex; i++)
                itemY += entries.get(i).getItemHeight();
            int itemHeight = item.getItemHeight() - 4;
            int itemWidth = this.getItemWidth();
            int itemMinX, itemMaxX;
            
            int y = this.getRowTop(renderIndex);
            int x = this.getRowLeft();
            renderItem(graphics, item, renderIndex, y, x, itemWidth, itemHeight, int_3, int_4, Objects.equals(hoveredItem, item), float_1);
        }
    }
    
    protected void renderItem(EarlyGraphics graphics, E item, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float delta) {
        item.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isSelected, delta);
    }
    
    protected int getRowLeft() {
        return this.left + this.width / 2 - this.getItemWidth() / 2 + 2;
    }
    
    protected int getRowTop(int index) {
        int integer = top + 4 - (int) this.getScroll();
        for (int i = 0; i < entries.size() && i < index; i++)
            integer += entries.get(i).getItemHeight();
        return integer;
    }
    
    protected boolean isFocused() {
        return false;
    }
    
    protected void renderHoleBackground(EarlyGraphics graphics, int y1, int y2, int alpha) {
        graphics.bindTexture(backgroundLocation);
        graphics.innerBlit(left, left + width, y1, y2, 0, 0, ((float) this.width / 32.0F), ((float) y1 / 32.0F), ((float) y2 / 32.0F),
                alpha << 24 | 64 << 16 | 64 << 8 | 64);
    }
    
    protected E remove(int int_1) {
        E itemListWidget$Item_1 = this.entries.get(int_1);
        return this.removeEntry(this.entries.get(int_1)) ? itemListWidget$Item_1 : null;
    }
    
    protected boolean removeEntry(E itemListWidget$Item_1) {
        return this.entries.remove(itemListWidget$Item_1);
    }
    
    public static final class SmoothScrollingSettings {
        public static final double CLAMP_EXTENSION = 200;
        
        private SmoothScrollingSettings() {}
    }
    
    @Environment(EnvType.CLIENT)
    public abstract static class Entry<E extends Entry<E>> {
        @Deprecated DynamicErrorFreeEntryListWidget<E> parent;
        
        public Entry() {
        }
        
        public abstract void render(EarlyGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta);
        
        public boolean isMouseOver(double double_1, double double_2) {
            return Objects.equals(this.parent.getItemAtPosition(double_1, double_2), this);
        }
        
        public DynamicErrorFreeEntryListWidget<E> getParent() {
            return parent;
        }
        
        public void setParent(DynamicErrorFreeEntryListWidget<E> parent) {
            this.parent = parent;
        }
        
        public abstract int getItemHeight();
        
        @Deprecated
        public int getMorePossibleHeight() {
            return -1;
        }
    
        public boolean mouseClicked(double double_1, double double_2, int int_1) {
            return false;
        }
    }
    
    @Environment(EnvType.CLIENT)
    class Entries extends AbstractList<E> {
        private final ArrayList<E> items;
        
        private Entries() {
            this.items = Lists.newArrayList();
        }
        
        @Override
        public void clear() {
            items.clear();
        }
        
        @Override
        public E get(int int_1) {
            return this.items.get(int_1);
        }
        
        @Override
        public int size() {
            return this.items.size();
        }
        
        @Override
        public E set(int int_1, E itemListWidget$Item_1) {
            E itemListWidget$Item_2 = this.items.set(int_1, itemListWidget$Item_1);
            itemListWidget$Item_1.parent = DynamicErrorFreeEntryListWidget.this;
            return itemListWidget$Item_2;
        }
        
        @Override
        public void add(int int_1, E itemListWidget$Item_1) {
            this.items.add(int_1, itemListWidget$Item_1);
            itemListWidget$Item_1.parent = DynamicErrorFreeEntryListWidget.this;
        }
        
        @Override
        public E remove(int int_1) {
            return this.items.remove(int_1);
        }
    }
}