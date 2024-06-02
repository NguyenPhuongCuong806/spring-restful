package vn.hoidanit.jobhunter.controller;

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

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    private final SkillRepository skillRepository;

    public SkillController(SkillService skillService, SkillRepository skillRepository) {
        this.skillService = skillService;
        this.skillRepository = skillRepository;
    }

    @PostMapping("/skills")
    @ApiMessage("create skill is success!")
    public ResponseEntity<Skill> handleCreateSkill(@RequestBody @Valid Skill skill) throws IdInvalidException {
        Optional<Skill> optional = this.skillRepository.findByName(skill.getName());
        if (optional.isPresent()) {
            throw new IdInvalidException("Tên kỹ năng đã tồn tại");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(this.skillService.handleCreateSkill(skill.getName()));
        }
    }

    @PutMapping("/skills")
    @ApiMessage("update skill is success!")
    public ResponseEntity<Skill> handleUpdateSkill(@RequestBody @Valid Skill skill) throws IdInvalidException {
        Optional<Skill> optional = this.skillRepository.findById(skill.getId());
        if (optional.isPresent()) {
            Optional<Skill> optionalname = this.skillRepository.findByName(skill.getName());
            if (optionalname.isPresent()) {
                throw new IdInvalidException("Tên kỹ năng đã tồn tại");
            }
            return ResponseEntity.status(HttpStatus.OK).body(this.skillService.handleUpdateSkill(skill));
        } else {
            throw new IdInvalidException("skill không tồn tại");
        }
    }

    @GetMapping("/skills")
    public ResponseEntity<ResultPaginationDTO> handleFetchAllSkill(Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.handleFetchAllSkill(pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Skill> skill = this.skillRepository.findById(id);

        if (skill.isEmpty()) {
            throw new IdInvalidException("skill không tồn tại");
        }

        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

}
