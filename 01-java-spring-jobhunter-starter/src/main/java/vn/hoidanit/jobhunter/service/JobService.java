package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResFetchJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;

    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Job handleCreateJob(Job job) {
        List<Long> listidjob = new ArrayList<>();
        List<Skill> skills = job.getSkills();
        for (Skill skill : skills) {
            listidjob.add(skill.getId());
        }

        if (job.getCompany() != null) {
            Optional<Company> optional = this.companyRepository.findById(job.getCompany().getId());
            if (optional.isPresent()) {
                job.setCompany(optional.get());
            }
        }

        List<Skill> listskill = this.skillRepository.findByIdIn(listidjob);
        job.setSkills(listskill);
        return this.jobRepository.save(job);
    }

    public Job handleUpdateJob(Job j, Job job) {

        if (j.getSkills() != null) {
            List<Long> reqSkill = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
        }

        if (j.getCompany() != null) {
            Optional<Company> optional = this.companyRepository.findById(j.getCompany().getId());
            if (optional.isPresent()) {
                job.setCompany(optional.get());
            }
        }
        job.setName(j.getName());
        job.setSalary(j.getSalary());
        job.setQuantity(j.getQuantity());
        job.setLocation(j.getLocation());
        job.setLevel(j.getLevel());
        job.setStartDate(j.getStartDate());
        job.setEndDate(j.getEndDate());
        job.setActive(j.isActive());

        return this.jobRepository.save(job);

    }

    public ResultPaginationDTO handleFetchAllJob(Pageable pageable) {
        Page<Job> page = this.jobRepository.findAll(pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);

        List<ResFetchJobDTO> jobDTOs = new ArrayList<ResFetchJobDTO>();

        for (Job job : page.getContent()) {
            ResFetchJobDTO dto = this.converFetchJob(job);
            jobDTOs.add(dto);
        }

        rs.setResult(jobDTOs);

        return rs;
    }

    public Job handleFetchJob(long id) {
        return this.jobRepository.findById(id).get();
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
        ;
    }

    public ResCreateJobDTO convertCreateJob(Job job) {
        ResCreateJobDTO createJobDTO = new ResCreateJobDTO();
        createJobDTO.setName(job.getName());
        createJobDTO.setActive(job.isActive());
        createJobDTO.setCreatedAt(job.getCreatedAt());
        createJobDTO.setCreatedBy(job.getCreatedBy());
        createJobDTO.setDescription(job.getDescription());
        createJobDTO.setEndDate(job.getEndDate());
        createJobDTO.setStartDate(job.getStartDate());
        createJobDTO.setLevel(job.getLevel());
        createJobDTO.setLocation(job.getLocation());
        createJobDTO.setQuantity(job.getQuantity());
        createJobDTO.setSalary(job.getSalary());
        List<Long> listidjob = new ArrayList<>();
        for (Skill skill : job.getSkills()) {
            listidjob.add(skill.getId());
        }
        List<Skill> listskill = this.skillRepository.findByIdIn(listidjob);

        List<String> listNameJob = new ArrayList<>();

        for (Skill skill : listskill) {
            listNameJob.add(skill.getName());
        }
        createJobDTO.setSkills(listNameJob);
        return createJobDTO;
    }

    public ResUpdateJobDTO convertUpdateJob(Job job) {
        ResUpdateJobDTO createJobDTO = new ResUpdateJobDTO();
        createJobDTO.setName(job.getName());
        createJobDTO.setActive(job.isActive());
        createJobDTO.setUpdatedAt(job.getUpdatedAt());
        createJobDTO.setUpdatedBy(job.getUpdatedBy());
        createJobDTO.setDescription(job.getDescription());
        createJobDTO.setEndDate(job.getEndDate());
        createJobDTO.setStartDate(job.getStartDate());
        createJobDTO.setLevel(job.getLevel());
        createJobDTO.setLocation(job.getLocation());
        createJobDTO.setQuantity(job.getQuantity());
        createJobDTO.setSalary(job.getSalary());
        List<Long> listidjob = new ArrayList<>();
        for (Skill skill : job.getSkills()) {
            listidjob.add(skill.getId());
        }
        List<Skill> listskill = this.skillRepository.findByIdIn(listidjob);

        List<String> listNameJob = new ArrayList<>();

        for (Skill skill : listskill) {
            listNameJob.add(skill.getName());
        }
        createJobDTO.setSkills(listNameJob);
        return createJobDTO;
    }

    public ResFetchJobDTO converFetchJob(Job job) {
        ResFetchJobDTO createJobDTO = new ResFetchJobDTO();
        createJobDTO.setId(job.getId());
        createJobDTO.setName(job.getName());
        createJobDTO.setActive(job.isActive());
        createJobDTO.setUpdatedAt(job.getUpdatedAt());
        createJobDTO.setUpdatedBy(job.getUpdatedBy());
        createJobDTO.setCreatedAt(job.getCreatedAt());
        createJobDTO.setCreatedBy(job.getCreatedBy());
        createJobDTO.setDescription(job.getDescription());
        createJobDTO.setEndDate(job.getEndDate());
        createJobDTO.setStartDate(job.getStartDate());
        createJobDTO.setLevel(job.getLevel());
        createJobDTO.setLocation(job.getLocation());
        createJobDTO.setQuantity(job.getQuantity());
        createJobDTO.setSalary(job.getSalary());

        ResFetchJobDTO.Company company = new ResFetchJobDTO.Company();
        company.setId(job.getCompany().getId());
        company.setName(job.getCompany().getName());
        company.setLogo(job.getCompany().getLogo());
        createJobDTO.setCompany(company);

        List<Long> listidjob = new ArrayList<>();
        for (Skill skill : job.getSkills()) {
            listidjob.add(skill.getId());
        }

        List<Skill> listskill = this.skillRepository.findByIdIn(listidjob);

        // List<String> listNameJob = new ArrayList<>();

        // for (Skill skill : listskill) {
        // listNameJob.add(skill.getName());
        // }
        createJobDTO.setSkills(listskill);
        return createJobDTO;
    }
}
