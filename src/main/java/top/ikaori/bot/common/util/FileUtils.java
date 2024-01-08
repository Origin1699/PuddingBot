package top.ikaori.bot.common.util;

import top.ikaori.bot.common.constant.Constant;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;

/**
 * @author origin
 */
public class FileUtils {

    public static String readFile(String file) {
        return readFile(new File(file));
    }

    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try (InputStream input = Files.newInputStream(file.toPath())) {
            IOUtils.lineIterator(input, Constant.DEFAULT_CHARSET).forEachRemaining(line -> {
                builder.append(line).append(System.lineSeparator());
            });
        } catch (IOException e) {

        }
        return builder.toString();
    }

    public static String toBase64(File file) {
        try {
            byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
            return new String(Base64.getEncoder().encode(bytes), Constant.DEFAULT_CHARSET);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
