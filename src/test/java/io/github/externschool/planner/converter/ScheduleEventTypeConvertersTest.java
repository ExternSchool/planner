package io.github.externschool.planner.converter;

import io.github.externschool.planner.dto.ScheduleEventTypeDTO;
import io.github.externschool.planner.entity.schedule.ScheduleEventType;
import io.github.externschool.planner.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleEventTypeConvertersTest {
    @Autowired private ConversionService conversionService;
    @Autowired private RoleService roleService;

    @Test
    public void shouldReturnDTO_whenConvertFromEventType() {
        ScheduleEventType eventType = new ScheduleEventType("newType", 2);
        eventType.setId(1L);
        Arrays.asList(roleService.getRoleByName("ROLE_ADMIN"), roleService.getRoleByName("ROLE_TEACHER"))
                .forEach(eventType::addOwner);
        Arrays.asList(roleService.getRoleByName("ROLE_ADMIN"),
                roleService.getRoleByName("ROLE_GUEST"),
                roleService.getRoleByName("ROLE_OFFICER"),
                roleService.getRoleByName("ROLE_STUDENT"),
                roleService.getRoleByName("ROLE_TEACHER"))
                .forEach(eventType::addParticipant);

        ScheduleEventTypeDTO expectedDTO = new ScheduleEventTypeDTO(
                eventType.getId(),
                eventType.getName(),
                eventType.getAmountOfParticipants(),
                new ArrayList<>(eventType.getOwners()),
                new ArrayList<>(eventType.getParticipants()));

        ScheduleEventTypeDTO actualDTO = conversionService.convert(eventType, ScheduleEventTypeDTO.class);

        assertThat(actualDTO)
                .isNotNull()
                .isEqualToComparingFieldByFieldRecursively(expectedDTO);
    }

    @Test
    public void shouldReturnEventType_whenConvertFromDTO() {
        ScheduleEventTypeDTO typeDTO = new ScheduleEventTypeDTO(
                1L,
                "newType",
                2,
                Collections.singletonList(roleService.getAllRoles().get(0)),
                roleService.getAllRoles());

        ScheduleEventType expectedType = new ScheduleEventType(typeDTO.getName(), typeDTO.getCountOfParticipant());
        expectedType.setId(typeDTO.getId());
        expectedType.addOwner(roleService.getAllRoles().get(0));
        roleService.getAllRoles().forEach(expectedType::addParticipant);

        ScheduleEventType actualType = conversionService.convert(typeDTO, ScheduleEventType.class);

        assertThat(actualType)
                .isNotNull()
                .isEqualToComparingFieldByFieldRecursively(expectedType);
    }
}
