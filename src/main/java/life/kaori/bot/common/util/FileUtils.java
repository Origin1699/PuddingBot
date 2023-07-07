package life.kaori.bot.common.util;

import life.kaori.bot.common.constant.Constant;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 *
 * author: origin
 */
public class FileUtils {
    public static String readFile(String file) {
        return readFile(new File(file));
    }

    public static String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try (InputStream input = Files.newInputStream(file.toPath())) {
            IOUtils.lineIterator(input, Constant.DEFAULT_CHARSET).forEachRemaining(line -> {
                builder.append(line).append(IOUtils.LINE_SEPARATOR);
            });
        } catch (IOException e) {

        }
        return builder.toString();
    }
}
