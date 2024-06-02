package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class FindUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Company company;
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Setter
    public static class Company {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class Role {
        private long id;
        private String name;
    }
}
