package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("create role is success!")
    public ResponseEntity<Role> handleCreateRole(@RequestBody Role role) throws IdInvalidException {
        if (this.roleService.isExistsRole(role.getName())) {
            throw new IdInvalidException("role đã tồn tại");
        }
        return ResponseEntity.ok().body(this.roleService.handleCreateRole(role));
    }

    @PutMapping("/roles")
    @ApiMessage("update role is success!")
    public ResponseEntity<Role> handleUpdateRole(@RequestBody Role role) throws IdInvalidException {
        if (Long.valueOf(role.getId()) == null || this.roleService.fetchRoleById(role.getId()).isEmpty()) {
            throw new IdInvalidException("role không tồn tại");
        } else {
            // if (this.roleService.isExistsRole(role.getName())) {
            // throw new IdInvalidException("role đã tồn tại");
            // }
            return ResponseEntity.ok().body(this.roleService.handUpdateRole(role));
        }

    }

    @GetMapping("/roles")
    @ApiMessage("fetch all role is success!")
    public ResponseEntity<ResultPaginationDTO> handleFetchAllRole(
            @Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.handleFetchAllRole(spec, pageable));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("delete role is success!")
    public ResponseEntity<Void> handleDeleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Role> optional = this.roleService.fetchRoleById(id);
        if (optional.isEmpty()) {
            throw new IdInvalidException("role không tồn tại");
        }
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("fetch role by id!")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Role> role = this.roleService.fetchRoleById(id);

        if (role.isEmpty()) {
            throw new IdInvalidException("Resume với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(role.get());
    }

}
