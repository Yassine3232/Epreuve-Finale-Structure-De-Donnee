package mv.sdd.utils;

import java.io.PrintWriter;

// NE PAS MODIFIER
public class Logger {
    private final PrintWriter out;
    private final boolean echoConsole;

    public Logger(PrintWriter out, boolean echoConsole) {
        this.out = out;
        this.echoConsole = echoConsole;
    }

    public void logLine(String message) {
        out.println(message);
        if (echoConsole) {
            System.out.println(message);
        }
    }

    public void logEmpty() {
        logLine("");
    }
}
