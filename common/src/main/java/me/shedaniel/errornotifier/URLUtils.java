package me.shedaniel.errornotifier;

import org.apache.commons.io.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
 * Copyright 2016 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class URLUtils {
    public static void openForked(String url) throws IOException, InterruptedException {
        Path javaBinDir = Paths.get(System.getProperty("java.home"), "bin");
        Path javaPath = Stream.of("javaw.exe", "java.exe", "java")
                .map(javaBinDir::resolve)
                .filter(Files::isRegularFile)
                .findFirst()
                .orElseThrow(() -> new IOException("Could not find java executable"));
        
        Path tmpPath = Files.createTempFile(null, ".jar");
        tmpPath.toFile().deleteOnExit();
        
        InputStream stream = ErrorNotifierPlatform.getResourceResolver().resolve("me/shedaniel/errornotifier/URLOpener.class");
        Files.deleteIfExists(tmpPath);
        try (ZipOutputStream outputStream = new ZipOutputStream(Files.newOutputStream(tmpPath))) {
            outputStream.putNextEntry(new ZipEntry("me/shedaniel/errornotifier/URLOpener.class"));
            outputStream.write(IOUtils.toByteArray(stream));
            outputStream.closeEntry();
        }
        
        Process process = new ProcessBuilder(javaPath.toAbsolutePath().normalize().toString(), "-Xmx100M", "-cp", tmpPath.toAbsolutePath().toString(), "me.shedaniel.errornotifier.URLOpener")
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();
        
        try (DataOutputStream os = new DataOutputStream(process.getOutputStream())) {
            os.writeUTF(url);
        }
        
        int exitCode = process.waitFor();
        if (exitCode != 0) throw new IOException("subprocess exited with code " + exitCode);
    }
}
