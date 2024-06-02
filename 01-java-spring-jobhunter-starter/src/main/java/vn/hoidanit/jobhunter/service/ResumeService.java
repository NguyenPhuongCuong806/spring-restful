package vn.hoidanit.jobhunter.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.util.SecurityUntil;

@Service
public class ResumeService {
    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Optional<Resume> handlefetchResume(long id) {
        return this.resumeRepository.findById(id);
    }

    public Resume handleCreateResume(Resume resume) {
        return this.resumeRepository.save(resume);
    }

    public Resume handleUpdateResume(Resume resume) {
        Optional<Resume> optional = this.handlefetchResume(resume.getId());
        if (optional.isPresent()) {
            optional.get().setStatus(resume.getStatus());
            return this.resumeRepository.save(optional.get());
        }
        return null;
    }

    public ResCreateResumeDTO convertResumeCreate(Resume resume) {
        ResCreateResumeDTO createResumeDTO = new ResCreateResumeDTO();

        createResumeDTO.setId(resume.getId());
        createResumeDTO.setCreatedAt(resume.getCreatedAt());
        createResumeDTO.setCreatedBy(resume.getCreatedBy());

        return createResumeDTO;
    }

    public ResUpdateResumeDTO convertResumeUpdate(Resume resume) {
        ResUpdateResumeDTO resUpdateResumeDTO = new ResUpdateResumeDTO();

        resUpdateResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resUpdateResumeDTO.setUpdatedBy(resume.getUpdatedBy());

        return resUpdateResumeDTO;
    }

    public ResFetchResumeDTO convertfetchResume(Resume resume) {
        ResFetchResumeDTO fetchResumeDTO = new ResFetchResumeDTO();

        fetchResumeDTO.setId(resume.getId());
        fetchResumeDTO.setEmail(resume.getEmail());
        fetchResumeDTO.setCreatedAt(resume.getCreatedAt());
        fetchResumeDTO.setCreatedBy(resume.getCreatedBy());
        fetchResumeDTO.setStatus(resume.getStatus());
        fetchResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        fetchResumeDTO.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getJob() != null) {
            fetchResumeDTO.setCompanyName(resume.getJob().getCompany().getName());
        }

        ResFetchResumeDTO.User user = new ResFetchResumeDTO.User();
        user.setId(resume.getUser().getId());
        user.setName(resume.getUser().getName());
        fetchResumeDTO.setUser(user);
        ResFetchResumeDTO.Job job = new ResFetchResumeDTO.Job();
        job.setId(resume.getJob().getId());
        job.setName(resume.getJob().getName());
        fetchResumeDTO.setJob(job);

        return fetchResumeDTO;
    }

    public ResultPaginationDTO handlefetchAllResume(Pageable pageable, Specification<Resume> spec) {
        Page<Resume> page = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);

        List<ResFetchResumeDTO> list = page.getContent().stream()
                .map(item -> this.convertfetchResume(item)).collect(Collectors.toList());

        rs.setResult(list);

        return rs;
    }

    public ResultPaginationDTO fetchResumesByUser(Pageable pageable) {
        String email = SecurityUntil.getCurrentUserLogin().isPresent() == true
                ? SecurityUntil.getCurrentUserLogin().get()
                : "";
        FilterNode node = filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = filterSpecificationConverter.convert(node);
        Page<Resume> page = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);

        List<ResFetchResumeDTO> list = page.getContent().stream()
                .map(item -> this.convertfetchResume(item)).collect(Collectors.toList());

        rs.setResult(list);

        return rs;
    }

}
