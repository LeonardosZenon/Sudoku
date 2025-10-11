package gr.leonzch.sudoku.utils.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logging {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static LocalDateTime now;

    private Logging(){}

    public static void log(LoggingTypes logType, String message) {
        now = LocalDateTime.now();
        System.out.printf("[%s]:[%s]: %s%n", now.format(formatter), logType, message);
    }

    public static void log(LoggingTypes logType, String message, Throwable throwable) {
        now = LocalDateTime.now();
        StringBuilder stackTrace = new StringBuilder();

        if (throwable != null) {
            stackTrace.append(throwable.toString()).append("\n");
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append("\tat ").append(element.toString()).append("\n");
            }
        }

        System.out.printf("[%s]:[%s]: %s%n\t%s", now.format(formatter), logType, message, stackTrace.toString());
    }
}
