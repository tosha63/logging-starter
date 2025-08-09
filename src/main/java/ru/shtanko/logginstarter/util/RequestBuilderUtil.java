package ru.shtanko.logginstarter.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class RequestBuilderUtil {

    public static String inlineRequestHeaders(HttpServletRequest request) {
        Map<String, String> headersMap = Collections.list(request.getHeaderNames()).stream()
             .collect(Collectors.toMap(it -> it, request::getHeader));
        return formatHeaders(headersMap);
    }

    public static String inlineResponseHeaders(HttpServletResponse response) {
        Map<String, String> headersMap = response.getHeaderNames().stream()
              .collect(Collectors.toMap(it -> it, response::getHeader));
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
}
