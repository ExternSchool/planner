package io.github.externschool.planner.util;

import io.github.externschool.planner.dto.PersonDTO;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static List<? extends PersonDTO> searchRequestFilter(Collection<? extends PersonDTO> persons, String request) {
        return  persons.stream()
                .filter(dto -> dto.getLastName() != null && dto.getLastName().contains(request)
                        || dto.getFirstName() != null && dto.getFirstName().contains(request)
                        || dto.getPatronymicName() != null && dto.getPatronymicName().contains(request)
                        || dto.getPhoneNumber() != null && dto.getPhoneNumber().contains(request))
                .collect(Collectors.toList());
    }
}
