package com.home.trip.util;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserUtil {
    public <T> String userRoleToStringWithComma(Collection<T> source, Function<T, String> extractor) {
        return source.stream()
                .map(extractor)
                .collect(Collectors.joining(","));
    }
}
