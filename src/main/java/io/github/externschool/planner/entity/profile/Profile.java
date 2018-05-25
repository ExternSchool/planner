package io.github.externschool.planner.entity.profile;

import javax.persistence.*;

@Entity
@Table(name = "profile")
@Inheritance(strategy = InheritanceType.JOINED)
public class Profile {

    @Id
    @Column(name = "profile_id" )
    private Long id;

    private Long validationKey;

    private String firstName;

    private String patronomycName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String password;

    private String encreptedPassword;

    //private Authority authority;

    ///constructor

    ///getters and setters
}
