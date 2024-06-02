package vn.hoidanit.jobhunter.domain.response.resume;

import java.time.Instant;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.util.constant.ResumeEnum;

@Getter
@Setter
public class ResFetchResumeDTO {

    private long id;

    private String email;

    private String url;
    private ResumeEnum status;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private String companyName;
    private User user;
    private Job job;

    @Getter
    @Setter
    public static class User {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class Job {
        private long id;
        private String name;
    }

}
