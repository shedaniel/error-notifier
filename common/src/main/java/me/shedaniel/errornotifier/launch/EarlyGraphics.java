package me.shedaniel.errornotifier.launch;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.shedaniel.errornotifier.ErrorNotifierPlatform;
import me.shedaniel.errornotifier.launch.early.*;
import me.shedaniel.errornotifier.launch.render.EarlyBufferBuilder;
import me.shedaniel.errornotifier.launch.render.EarlyDrawType;
import me.shedaniel.errornotifier.launch.render.EarlyRenderFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

public enum EarlyGraphics {
    INSTANCE;
    
    public static final Logger LOGGER = LogManager.getLogger(EarlyGraphics.class);
    public static final Map<String, Texture> textures = new HashMap<>();
    public static Font font;
    
    public static Font getFont() {
        if (font == null) {
            JsonElement element = JsonParser.parseString("""
                    {"type":"bitmap","file":"/assets/minecraft/textures/font/ascii.png","ascent":7,"chars":["\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000","\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000"," !\\"#$%&'()*+,-./","0123456789:;<=>?","@ABCDEFGHIJKLMNO","PQRSTUVWXYZ[\\\\]^_","`abcdefghijklmno","pqrstuvwxyz{|}~\\u0000","\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000","\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000£\\u0000\\u0000ƒ","\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000ªº\\u0000\\u0000¬\\u0000\\u0000\\u0000«»","░▒▓│┤╡╢╖╕╣║╗╝╜╛┐","└┴┬├─┼╞╟╚╔╩╦╠═╬╧","╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀","\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000∅∈\\u0000","≡±≥≤⌠⌡÷≈°∙\\u0000√\\u207f²■\\u0000"]}""");
            FontLoader loader = FontLoader.fromJson(element.getAsJsonObject(), ErrorNotifierPlatform.getResourceResolver());
            font = new Font(loader);
        }
        
        return font;
    }
    
    public void fill(int x1, int y1, int x2, int y2, int color) {
        int tmp;
        
        if (x1 > x2) {
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        
        if (y1 > y2) {
            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        EarlyBufferBuilder builder = new EarlyBufferBuilder("position_color", EarlyRenderFormat.POSITION_COLOR);
        float rF = ColorUtil.rF(color);
        float gF = ColorUtil.gF(color);
        float bF = ColorUtil.bF(color);
        float aF = ColorUtil.aF(color);
        builder.pos(x1, y2, 0.0f).color(rF, gF, bF, aF).endVertex();
        builder.pos(x2, y2, 0.0f).color(rF, gF, bF, aF).endVertex();
        builder.pos(x2, y1, 0.0f).color(rF, gF, bF, aF).endVertex();
        builder.pos(x1, y1, 0.0f).color(rF, gF, bF, aF).endVertex();
        builder.end(EarlyDrawType.QUAD);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    public void fillGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
        int tmp;
        
        if (x1 > x2) {
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        
        if (y1 > y2) {
            tmp = y1;
            y1 = y2;
            y2 = tmp;
            tmp = color1;
            color1 = color2;
            color2 = tmp;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        EarlyBufferBuilder builder = new EarlyBufferBuilder("position_color", EarlyRenderFormat.POSITION_COLOR);
        float rF1 = ColorUtil.rF(color1);
        float gF1 = ColorUtil.gF(color1);
        float bF1 = ColorUtil.bF(color1);
        float aF1 = ColorUtil.aF(color1);
        float rF2 = ColorUtil.rF(color2);
        float gF2 = ColorUtil.gF(color2);
        float bF2 = ColorUtil.bF(color2);
        float aF2 = ColorUtil.aF(color2);
        builder.pos(x1, y2, 0.0f).color(rF2, gF2, bF2, aF2).endVertex();
        builder.pos(x2, y2, 0.0f).color(rF2, gF2, bF2, aF2).endVertex();
        builder.pos(x2, y1, 0.0f).color(rF1, gF1, bF1, aF1).endVertex();
        builder.pos(x1, y1, 0.0f).color(rF1, gF1, bF1, aF1).endVertex();
        builder.end(EarlyDrawType.QUAD);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    public void bindTexture(String textureId) {
        _bindTexture(convertToPath(textureId));
    }
    
    private String convertToPath(String textureId) {
        int column = textureId.indexOf(':');
        String ns, path;
        if (column == -1) {
            ns = "minecraft";
            path = textureId;
        } else {
            ns = textureId.substring(0, column);
            path = textureId.substring(column + 1);
        }
        return "/assets/%s/%s".formatted(ns, path);
    }
    
    public boolean bindTextureCustomStream(String textureId, Supplier<InputStream> supplier) {
        return registerTexture(convertToPath(textureId), supplier);
    }
    
    public float drawString(String string, int x, int y, int color) {
        return getFont().draw(string, x, y, color, 1.0F);
    }
    
    public float drawStringCentered(String string, int x, int y, int color) {
        return getFont().draw(string, x - width(string) / 2, y, color, 1.0F);
    }
    
    public float drawStringWithShadow(String string, int x, int y, int color) {
        getFont().draw(string, x + 1, y + 1, color, 3 / 9F);
        return getFont().draw(string, x, y, color, 1.0F);
    }
    
    public float drawStringCenteredWithShadow(String string, int x, int y, int color) {
        getFont().draw(string, x - width(string) / 2 + 1, y + 1, color, 3 / 9F);
        return getFont().draw(string, x - width(string) / 2, y, color, 1.0F);
    }
    
    public int width(String string) {
        return getFont().width(string);
    }
    
    public List<String> splitString(String string, int width) {
        return getFont().split(string, width);
    }
    
    public int getScaledWidth() {
        return (int) Math.ceil(EarlyWindow.framebufferWidth / EarlyWindow.scale);
    }
    
    public int getScaledHeight() {
        return (int) Math.ceil(EarlyWindow.framebufferHeight / EarlyWindow.scale);
    }
    
    public void innerBlit(int x1, int x2, int y1, int y2, int z, float u1, float u2, float v1, float v2, int color) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        float rF = ColorUtil.rF(color);
        float gF = ColorUtil.gF(color);
        float bF = ColorUtil.bF(color);
        float aF = ColorUtil.aF(color);
        EarlyBufferBuilder builder = new EarlyBufferBuilder("position_color_tex", EarlyRenderFormat.POSITION_COLOR_TEX);
        builder.pos(x1, y2, z).color(rF, gF, bF, aF).tex(u1, v2).endVertex();
        builder.pos(x2, y2, z).color(rF, gF, bF, aF).tex(u2, v2).endVertex();
        builder.pos(x2, y1, z).color(rF, gF, bF, aF).tex(u2, v1).endVertex();
        builder.pos(x1, y1, z).color(rF, gF, bF, aF).tex(u1, v1).endVertex();
        builder.end(EarlyDrawType.QUAD);
        GL11.glDisable(GL11.GL_BLEND);
    }
    
    public static void registerTexture(String name) {
        if (!textures.containsKey(name)) {
            registerTexture(name, () -> Objects.requireNonNull(ErrorNotifierPlatform.getResourceResolver().resolve(name), name + " not found!"));
        }
    }
    
    public static boolean registerTexture(String name, Supplier<InputStream> stream) {
        if (!textures.containsKey(name)) {
            try {
                ImageTexture texture = new ImageTexture(Image.load(stream.get()));
                registerTexture(name, texture);
            } catch (IOException e) {
                LOGGER.error("Failed to load texture " + name, e);
                return false;
            }
        }
        _bindTexture(name);
        return true;
    }
    
    public static void registerTexture(String name, Texture texture) {
        if (!textures.containsKey(name)) {
            texture.upload(false, false);
            textures.put(name, texture);
        }
    }
    
    public static void _bindTexture(String name) {
        registerTexture(name);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.get(name).getId());
    }
    
    public static class Font {
        private final List<FontTexture> textures = Lists.newArrayList();
        private final FontLoader loader;
        private final Int2ObjectMap<BakedGlyph> glyphs = new Int2ObjectOpenHashMap<>();
        private static final Glyph SPACE_GLYPH = new Glyph() {
            @Override
            public float getOversample() {
                return 1.0F;
            }
            
            @Override
            public int getPixelWidth() {
                return 4;
            }
            
            @Override
            public int getPixelHeight() {
                return 0;
            }
            
            @Override
            public float getAdvance() {
                return 4.0F;
            }
            
            @Override
            public void upload(int id, int xOffset, int yOffset, Image out) {
            }
            
            @Override
            public boolean isColored() {
                return true;
            }
        };
        
        public Font(FontLoader loader) {
            this.loader = loader;
        }
        
        public float draw(String text, float x, float y, int color, float dimFactor) {
            float[] xx = new float[]{x};
            text.codePoints().forEach(value -> {
                xx[0] += draw(xx[0], y, color, dimFactor, value);
            });
            return xx[0] - x;
        }
        
        public int width(String text) {
            return (int) Math.ceil(text.codePoints().mapToDouble(this::width).sum());
        }
        
        public float draw(float x, float y, int color, float dimFactor, int codepoint) {
            return draw(x, y, ColorUtil.rF(color), ColorUtil.gF(color), ColorUtil.bF(color), ColorUtil.aF(color), dimFactor, codepoint);
        }
        
        public List<String> split(String text, int width) {
            List<String> parts = Lists.newArrayList();
            text.codePoints().forEach(value -> {
                String part = parts.isEmpty() ? "" : parts.get(parts.size() - 1);
                String newPart = part + new String(new int[]{value}, 0, 1);
                if (width(newPart) > width || parts.isEmpty()) {
                    parts.add("");
                    newPart = new String(new int[]{value}, 0, 1);
                }
                parts.set(parts.size() - 1, newPart);
            });
            return parts;
        }
        
        public float draw(float x, float y, float r, float g, float b, float a, float dimFactor, int codepoint) {
            Glyph glyph = getRawGlyph(codepoint);
            BakedGlyph bakedGlyph = getGlyph(codepoint);
            r *= dimFactor;
            g *= dimFactor;
            b *= dimFactor;
            
            renderChar(bakedGlyph, false, x, y, r, g, b, a);
            
            return glyph.getAdvance();
        }
        
        public float width(int codepoint) {
            Glyph glyph = getRawGlyph(codepoint);
            return glyph.getAdvance();
        }
        
        void renderChar(BakedGlyph bakedGlyph, boolean italic, float x, float y, float r, float g, float b, float a) {
            bakedGlyph.render(italic, x, y, r, g, b, a);
        }
        
        public Glyph getRawGlyph(int i) {
            if (i == 32) return SPACE_GLYPH;
            Glyph glyph = loader.getGlyph(i);
            return glyph == null ? MissingGlyph.INSTANCE : glyph;
        }
        
        public BakedGlyph getGlyph(int i) {
            return this.glyphs.computeIfAbsent(i, (ix) -> {
                return this.stitch(getRawGlyph(ix));
            });
        }
        
        private BakedGlyph stitch(Glyph glyph) {
            Iterator<FontTexture> var2 = this.textures.iterator();
            BakedGlyph bakedGlyph;
            do {
                if (!var2.hasNext()) {
                    String newId = "font/" + System.identityHashCode(this) + "/" + this.textures.size();
                    FontTexture fontTexture = new FontTexture(newId, glyph.isColored());
                    this.textures.add(fontTexture);
                    registerTexture(newId, fontTexture);
                    BakedGlyph bakedGlyph2 = fontTexture.add(glyph);
                    return bakedGlyph2 == null ? stitch(MissingGlyph.INSTANCE) : bakedGlyph2;
                }
                
                FontTexture fontTexture = var2.next();
                bakedGlyph = fontTexture.add(glyph);
            } while (bakedGlyph == null);
            
            return bakedGlyph;
        }
    }
}
