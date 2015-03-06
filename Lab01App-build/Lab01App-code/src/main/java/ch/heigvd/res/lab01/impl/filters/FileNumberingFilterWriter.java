package ch.heigvd.res.lab01.impl.filters;

import edu.emory.mathcs.backport.java.util.Arrays;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

    private OS os = OS.Linux;
    private boolean startOfLine = true;
    private int n = 0;
    private int previousChar;

    private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());

    public FileNumberingFilterWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        str = str.substring(off, off + len);
        if (str.contains("\r\n")) {
            str = str.replaceAll("\r", "");
            os = OS.Windows;
        } else if (str.contains("\r")) {
            str = str.replaceAll("\r", "\n");
            os = OS.MacOS;
        }

        String line = "";
        for (String s : str.split("\n", -1)) {
            if (startOfLine) {
                line += ++n + "\t" + s;
                startOfLine = false;
            } else if (line.isEmpty()) {
                line += s;
            } else {
                switch (os) {
                    case Windows:
                        line += "\r\n" + ++n + "\t" + s;
                        break;
                    case MacOS:
                        line += "\r" + ++n + "\t" + s;
                        break;
                    case Linux:
                        line += "\n" + ++n + "\t" + s;
                }
            }
        }

        out.write(line);

        if (line.endsWith("\n") || line.endsWith("\r")) {
            startOfLine = true;
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        write(new String(cbuf), off, len);
    }

    @Override
    public void write(int c) throws IOException {
        if (previousChar == '\r' && c == '\n') {
            startOfLine = false;
        }

        if (startOfLine) {
            out.write(++n + "\t" + (char) c);
            startOfLine = false;
        } else {
            out.write(c);
        }

        if (c == '\n' || c == '\r') {
            startOfLine = true;
        }

        previousChar = c;
    }

    enum OS {

        Windows,
        MacOS,
        Linux
    }
}
