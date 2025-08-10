package ru.shtanko.logginstarter.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import ru.shtanko.logginstarter.mask.MaskerHeader;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class LoggingUtil {

    private static MaskerHeader maskerHeader;

    public static void setMasker(MaskerHeader maskerHeader) {
        LoggingUtil.maskerHeader = maskerHeader;
    }

    public static String inlineRequestHeaders(HttpServletRequest request) {
        Map<String, String> headersMap = Collections.list(request.getHeaderNames()).stream()
             .collect(Collectors.toMap(it -> it, request::getHeader));

        headersMap = maskIfNeeded(headersMap);

        return formatHeaders(headersMap);
    }

    public static String inlineResponseHeaders(HttpServletResponse response) {
        Map<String, String> headersMap = response.getHeaderNames().stream()
              .collect(Collectors.toMap(it -> it, response::getHeader));

        headersMap = maskIfNeeded(headersMap);

        return formatHeaders(headersMap);
    }

    private static String formatHeaders(Map<String, String> headersMap) {
        String inlineHeaders = headersMap.entrySet().stream()
              .map(entry -> entry.getKey() + "=" + entry.getValue())
              .collect(Collectors.joining(","));
        return "headers={" + inlineHeaders + "}";
    }

    public static String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
               .map(qs -> "?" + qs)
               .orElse(Strings.EMPTY);
    }

    private static Map<String, String> maskIfNeeded(Map<String, String> headers) {
        return maskerHeader != null ? maskerHeader.maskHeaders(headers) : headers;
    }
}
