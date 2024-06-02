package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.FindUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill handleCreateSkill(String name) {
        Skill skill = new Skill();
        skill.setName(name);
        return this.skillRepository.save(skill);

    }

    public Skill handleUpdateSkill(Skill skill) {
        Optional<Skill> optional = this.skillRepository.findById(skill.getId());
        if (optional.isPresent()) {
            optional.get().setName(skill.getName());
            return this.skillRepository.save(optional.get());
        }
        return null;
    }

    public ResultPaginationDTO handleFetchAllSkill(Pageable pageable) {
        Page<Skill> page = this.skillRepository.findAll(pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);

        List<Skill> skills = new ArrayList<Skill>();

        for (Skill skill : page.getContent()) {
            skills.add(skill);
        }

        rs.setResult(skills);

        return rs;
    }

    public void deleteSkill(long id) {
        Optional<Skill> optional = this.skillRepository.findById(id);
        Skill skill = optional.get();

        skill.getJobs().forEach(job -> job.getSkills().remove(skill));

        this.skillRepository.delete(skill);
    }
}
