package gamein2.team.kernel.dto.request;


import gamein2.team.kernel.enums.Education;
import gamein2.team.kernel.enums.Gender;
import gamein2.team.kernel.enums.IntroductionMethod;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class ProfileInfoRequestDTO {
    private String username;
    private String persianName;
    private String persianSurname;
    private String englishName;
    private String englishSurname;
    private Gender gender;
    private LocalDate dob;
    private Education education;
    private String school;
    private String major;
    private Integer yearOfEntrance;
    private String province;
    private String city;
    private IntroductionMethod introductionMethod;
}
