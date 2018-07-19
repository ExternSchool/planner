package io.github.externschool.planner.bootstrapdata;

import io.github.externschool.planner.dto.UserDTO;
import io.github.externschool.planner.entity.Role;
import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.entity.profile.Teacher;
import io.github.externschool.planner.repository.UserRepository;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import io.github.externschool.planner.service.TeacherService;
import io.github.externschool.planner.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@ExcludeFromTests
public class BootstrapDataPopulator implements InitializingBean {
    private final UserService userService;
    private final TeacherService teacherService;
    private final SchoolSubjectService schoolSubjectService;
    private UserRepository userRepository;
    private RoleService roleService;

    public BootstrapDataPopulator(final UserService userService,
                                  final TeacherService teacherService,
                                  final SchoolSubjectService schoolSubjectService,
                                  final UserRepository userRepository,
                                  final RoleService roleService) {
        this.userService = userService;
        this.teacherService = teacherService;
        this.schoolSubjectService = schoolSubjectService;
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        createUser("q@q", "q", "ROLE_ADMIN");

        createTeacher(new Person(
                        null,
                        "James",
                        "Merrill",
                        "Carlsmith",
                        "(066)666-6666",
                        new VerificationKey()),
                "Psychologist",
                Collections.singletonList("Cognitive dissonance theory"));

        createTeacher(new Person(
                        null,
                        "Alexander",
                        "Sutherland",
                        "Neill",
                        "(099)999-9999",
                        new VerificationKey()),
                "Principal",
                Arrays.asList("Quantum Mechanics", "Algebraic topology"));

        createTeacher(new Person(
                        null,
                        "Leonardo",
                        "di ser Piero",
                        "da Vinci",
                        "(099)999-1111",
                        new VerificationKey()),
                "Teacher",
                Arrays.asList("Anatomy and physiology", "Rocket Science"));
    }

    private void createUser(String email, String password, String role) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setPassword(password);
        User user = userService.createNewUser(userDTO);
        user = addUserNewRoleByName(user, role);
    }

    private void createTeacher(Person person, String officerName, List<String> subjectsNames) {
        Teacher teacher = new Teacher(
                person.getId(),
                person.getFirstName(),
                person.getPatronymicName(),
                person.getLastName(),
                person.getPhoneNumber(),
                person.getVerificationKey(),
                officerName,
                new HashSet<>());
        for (String subjectName : subjectsNames) {
            SchoolSubject subject = new SchoolSubject();
            subject.setName(subjectName);
            schoolSubjectService.saveOrUpdateSubject(subject);
            teacher.addSubject(subject);
        }
        teacherService.saveOrUpdateTeacher(teacher);
    }

    //TODO Move to User Service
    private User addUserNewRoleByName(User user, String name) {
        Role role = roleService.getRoleByName(name);
        user.addRole(role);

        return userRepository.save(user);
    }
}
