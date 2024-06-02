package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ResumeController(ResumeService resumeService, UserRepository userRepository, JobRepository jobRepository) {
        this.resumeService = resumeService;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    @PostMapping("/resumes")
    @ApiMessage("create resume is success")
    public ResponseEntity<ResCreateResumeDTO> handleCreateResume(@RequestBody @Valid Resume resume)
            throws IdInvalidException {
        Optional<User> optionaluser = this.userRepository.findById(resume.getUser().getId());
        Optional<Job> optionaljob = this.jobRepository.findById(resume.getJob().getId());
        if (optionaluser.isEmpty()) {
            throw new IdInvalidException("user not found");
        }
        if (optionaljob.isEmpty()) {
            throw new IdInvalidException("job not found");
        }
        Resume resume2 = this.resumeService.handleCreateResume(resume);
        return ResponseEntity.ok().body(this.resumeService.convertResumeCreate(resume2));
    }

    @PutMapping("/resumes")
    @ApiMessage("update resume is success")
    public ResponseEntity<ResUpdateResumeDTO> handleUpdateResume(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> optional = this.resumeService.handlefetchResume(resume.getId());
        if (optional.isEmpty()) {
            throw new IdInvalidException("resume not found");
        }
        Resume resume2 = this.resumeService.handleUpdateResume(resume);
        return ResponseEntity.ok().body(this.resumeService.convertResumeUpdate(resume2));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("fetch resume is success")
    public ResponseEntity<ResFetchResumeDTO> handlefetchResume(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> optional = this.resumeService.handlefetchResume(id);
        if (optional.isEmpty()) {
            throw new IdInvalidException("resume not found");
        }
        Resume resume = this.resumeService.handlefetchResume(id).get();
        return ResponseEntity.ok().body(this.resumeService.convertfetchResume(resume));

    }

    @GetMapping("/resumes")
    @ApiMessage("fetch all resume")
    public ResponseEntity<ResultPaginationDTO> handlefetchAllResume(@Filter Specification<Resume> spec,
            Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.handlefetchAllResume(pageable, spec));

    }

    @PostMapping("resumes/by-user")
    @ApiMessage("get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumesByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumesByUser(pageable));
    }

}
