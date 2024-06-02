package vn.hoidanit.jobhunter.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResFetchJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;
    private final JobRepository jobRepository;

    public JobController(JobService jobService, JobRepository jobRepository) {
        this.jobService = jobService;
        this.jobRepository = jobRepository;
    }

    @PostMapping("/jobs")
    @ApiMessage("create job is success!")
    public ResponseEntity<ResCreateJobDTO> handleCreateJob(@RequestBody Job job) {
        Job job2 = this.jobService.handleCreateJob(job);
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.convertCreateJob(job2));
    }

    @PutMapping("/jobs")
    @ApiMessage("update job is success!")
    public ResponseEntity<ResUpdateJobDTO> handleUpdateJob(@RequestBody Job job) throws IdInvalidException {
        Optional<Job> optional = this.jobRepository.findById(job.getId());
        if (optional.isPresent()) {
            Job job2 = this.jobService.handleUpdateJob(job, optional.get());
            return ResponseEntity.status(HttpStatus.OK).body(this.jobService.convertUpdateJob(job2));
        } else {
            throw new IdInvalidException("không tìm thấy job để update");
        }

    }

    @GetMapping("/jobs")
    @ApiMessage("fetch all job is success!")
    public ResponseEntity<ResultPaginationDTO> handlefetchAllJob(Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.handleFetchAllJob(pageable));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("call api is success!")
    public ResponseEntity<ResFetchJobDTO> handlefetchJob(@PathVariable("id") Long id) throws IdInvalidException {
        Optional<Job> optional = this.jobRepository.findById(id);
        if (optional.isPresent()) {
            Job job = this.jobService.handleFetchJob(id);
            return ResponseEntity.status(HttpStatus.OK).body(this.jobService.converFetchJob(job));
        } else {
            throw new IdInvalidException("không tìm thấy data");
        }
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete a job by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> optional = this.jobRepository.findById(id);
        if (optional.isEmpty()) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok().body(null);
    }

}
