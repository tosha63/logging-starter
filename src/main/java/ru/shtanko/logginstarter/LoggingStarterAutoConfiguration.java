package ru.shtanko.logginstarter;

public class LoggingStarterAutoConfiguration {
    public static void println(String message) {
        System.out.printf("Выведено из gradle стартера: %s%n", message);
    }
}
