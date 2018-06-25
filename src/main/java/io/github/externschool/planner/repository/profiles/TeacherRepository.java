package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends CrudRepository<Teacher, Long> {

    List<Teacher> findAll();

    Teacher findTeacherById(Long id);

    @Query("SELECT t FROM Teacher t ORDER BY t.lastName, t.firstName")
    List<Teacher> findAllSortByLastNameAndFirstName();

}
