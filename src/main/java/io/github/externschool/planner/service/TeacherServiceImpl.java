package io.github.externschool.planner.service;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.course.Course;
import io.github.externschool.planner.entity.profile.Teacher;

import io.github.externschool.planner.repository.VerificationKeyRepository;
import io.github.externschool.planner.repository.profiles.TeacherRepository;
import io.github.externschool.planner.repository.schedule.ScheduleEventRepository;
import io.github.externschool.planner.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final   TeacherRepository teacherRepository;
    private final   VerificationKeyRepository keyRepository;
    private final   ScheduleService scheduleService;

    @Autowired
    public TeacherServiceImpl(final TeacherRepository teacherRepository,
                              ScheduleEventRepository scheduleEventRepository, final VerificationKeyRepository keyRepository, ScheduleService scheduleService) {
        this.teacherRepository = teacherRepository;
        this.keyRepository = keyRepository;
        this.scheduleService = scheduleService;

    @Override
    public Teacher findTeacherById(Long id) {
        return teacherRepository.findTeacherById(id);
    }

    @Override
    public List<Teacher> findAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public List<Teacher> findAllBySubject(SchoolSubject subject) {
        return teacherRepository.findAllBySubjectsContains(subject).stream()
                .sorted(Comparator.comparing(Teacher::getLastName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Teacher> findAllByLastName(final String lastName) {
        return teacherRepository.findAllByLastNameOrderByLastName(lastName);
    }

    @Override
    public List<Teacher> findAllByOrderByLastName() {
        return teacherRepository.findAllByOrderByLastName();
    }

    @Override
    public Teacher saveOrUpdateTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public void deleteTeacherById(Long id) {
        Teacher teacher = teacherRepository.findTeacherById(id);
        if(teacher != null) {
            Set<Course> courses = new HashSet<>(teacher.getCourses());
            for (Course course : courses) {
                teacher.removeCourse(course);
                courseRepository.save(course);
            }
            Set<SchoolSubject> subjects = new HashSet<>(teacher.getSubjects());
            for (SchoolSubject subject : subjects) {
                teacher.removeSubject(subject);
                teacherRepository.save(teacher);
            }
            Optional.ofNullable(teacher.getVerificationKey()).ifPresent(key -> {
                Optional.ofNullable(key.getUser()).ifPresent(User::removeVerificationKey);
                keyRepository.delete(key);
            });
            teacherRepository.deleteById(id);
        }
    }

    @Override
    @Scheduled(cron = "0 0 01 ? * SAT")
    public void updateTeacherSchedule(){

        List<Teacher> teachers = findAllTeachers();

        for (Teacher teacher: teachers
             ) {
            for (int i = 0; i < 5; i++) {

                List<ScheduleEvent> events = scheduleService
                                            .getEventsByOwnerAndDate(teacher.getVerificationKey().getUser()
                                            , Constants.FIRST_MONDAY_OF_EPOCH.plusDays(i));

                for (ScheduleEvent event: events
                     ) {
                    ScheduleEvent nextEvent = event;
                    nextEvent.setStartOfEvent(scheduleService.getNextWeekFirstDay().atStartOfDay());
                    scheduleService.saveEvent(nextEvent);
                }
            }
        }
    }
}
