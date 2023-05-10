package gamein2.team.kernel.entity;

import gamein2.team.kernel.dto.result.ProfileInfoDTO;
import gamein2.team.kernel.dto.result.TeamUserDTO;
import gamein2.team.kernel.dto.result.UserDTO;
import gamein2.team.kernel.enums.Education;
import gamein2.team.kernel.enums.Gender;
import gamein2.team.kernel.enums.IntroductionMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    private Team team;

    @Column(name = "phone", unique = true, nullable = false)
    private String phone;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "name",unique = true)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "is_admin", columnDefinition = "boolean default false")
    private boolean isAdmin = false;

    @Column(name = "persian_name")
    private String persianName;

    @Column(name = "persian_surname")
    private String persianSurname;

    @Column(name = "english_name")
    private String englishName;

    @Column(name = "english_surname")
    private String englishSurname;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "date_of_birth")
    private LocalDate dob;

    @Column(name = "education")
    @Enumerated(EnumType.STRING)
    private Education education;

    @Column(name = "school")
    private String school;

    @Column(name = "major")
    private String major;

    @Column(name = "year_of_entrance")
    private Integer yearOfEntrance;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "introduction_method")
    private IntroductionMethod introductionMethod;

    @Column(name = "password_code", unique = true)
    private String passwordCode;

    public Boolean isComplete() {
        return this.getCity() != null && this.getDob() != null && this.getEducation() != null && this.getGender() != null
                && this.getEnglishName() != null && this.getEnglishSurname() != null && this.getPersianName() != null
                && this.getPersianSurname() != null && this.getMajor() != null && this.getName() != null
                && this.getIntroductionMethod() != null && this.getProvince() != null && this.getSchool() != null
                && this.getYearOfEntrance() != null;
    }

    public UserDTO toDTO() {
        return new UserDTO(id, name, persianName, persianSurname, gender);
    }

    public ProfileInfoDTO toProfileDTO() {
        return new ProfileInfoDTO(isComplete(), name, persianName, persianSurname, englishName, englishSurname,
                gender, dob, education,
                school, major, yearOfEntrance, province, city, introductionMethod);
    }

    public TeamUserDTO userDTO(){
        return new TeamUserDTO(id, name, persianName, persianSurname, gender,email);
    }
}