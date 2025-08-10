package ru.shtanko.logginstarter.mask;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class MaskerHeader {

    private final Set<String> headersToMask;
    private static final String MASK = "***";

    public MaskerHeader(Set<String> headersToMask) {
        this.headersToMask = headersToMask != null ? headersToMask : Collections.emptySet();
    }

    public Map<String, String> maskHeaders(Map<String, String> headers) {
        if (headersToMask.isEmpty()) {
            return headers;
        }

        return headers.entrySet().stream()
              .collect(Collectors.toMap(
                  Map.Entry::getKey,
                  entry -> shouldMask(entry.getKey()) ? MASK : entry.getValue()));
    }

    private boolean shouldMask(String headerName) {
        return headersToMask.stream()
             .anyMatch(pattern -> headerName.matches(convertToRegex(pattern)));
    }

    private String convertToRegex(String pattern) {
        return pattern.replace("*", ".*");
    }
}
