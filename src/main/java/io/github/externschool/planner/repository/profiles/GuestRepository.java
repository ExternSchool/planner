package io.github.externschool.planner.repository.profiles;

import io.github.externschool.planner.entity.profile.Guest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends CrudRepository<Guest, Long> {

    List<Guest> findAll();
}
