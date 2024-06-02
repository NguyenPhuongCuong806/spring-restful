package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class CreateUserDTO {
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Company company;

    @Getter
    @Setter
    public static class Company {
        private long id;
        private String name;
    }

    private Instant createdAt;
}
