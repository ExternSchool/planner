package io.github.externschool.planner.entity;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "authority")

public class Authority {

    @Id
    @Column
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private List<User> users = new ArrayList<>();

    public Authority() {
    }

    public Authority(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(name, authority.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
