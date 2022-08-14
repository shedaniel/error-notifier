package me.shedaniel.errornotifier.launch;

import me.shedaniel.errornotifier.ErrorNotifierPlatform;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EarlyShader {
    private final int handle;
    private final ShaderType shaderType;
    
    public static void useShader(String shaderName) {
        EarlyShaderProgram.getProgram(shaderName).use();
    }
    
    public static int getUniform(String shaderName, String uniformName) {
        return EarlyShaderProgram.getProgram(shaderName).getUniform(uniformName);
    }
    
    static EarlyShader createVertexShader(String shaderName) {
        try (InputStream stream = ErrorNotifierPlatform.getResourceResolver().resolve("error_notifier_shaders/" + shaderName + ".vsh")) {
            return new EarlyShader(IOUtils.toString(stream, StandardCharsets.UTF_8), ShaderType.VERTEX);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    static EarlyShader createFragmentShader(String shaderName) {
        try (InputStream stream = ErrorNotifierPlatform.getResourceResolver().resolve("error_notifier_shaders/" + shaderName + ".fsh")) {
            return new EarlyShader(IOUtils.toString(stream, StandardCharsets.UTF_8), ShaderType.FRAGMENT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public EarlyShader(String shader, ShaderType shaderType) {
        handle = GL20.glCreateShader(shaderType == ShaderType.VERTEX ? GL20.GL_VERTEX_SHADER : GL20.GL_FRAGMENT_SHADER);
        this.shaderType = shaderType;
        GL20.glShaderSource(handle, "#version 150\n\n" + shader);
        GL20.glCompileShader(handle);
        
        String infoLog = GL20.glGetShaderInfoLog(handle, 512);
        System.out.println("Shader " + shaderType + " compilation status: " + infoLog);
    }
    
    int getHandle() {
        return handle;
    }
    
    public enum ShaderType {
        VERTEX,
        FRAGMENT
    }
}
