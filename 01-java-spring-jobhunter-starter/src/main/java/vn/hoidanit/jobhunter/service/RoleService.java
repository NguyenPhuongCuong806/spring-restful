package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role handleCreateRole(Role role) {

        List<Long> longs = role.getPermissions()
                .stream().map(x -> x.getId())
                .collect(Collectors.toList());

        List<Permission> permissions = this.permissionRepository.findByIdIn(longs);
        role.setPermissions(permissions);

        return this.roleRepository.save(role);
    }

    public Role handUpdateRole(Role role) {
        Optional<Role> optional = this.roleRepository.findById(role.getId());
        if (optional.isPresent()) {
            List<Long> longs = role.getPermissions()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());

            List<Permission> permissions = this.permissionRepository.findByIdIn(longs);
            role.setPermissions(permissions);
            role.setCreatedAt(optional.get().getCreatedAt());
            role.setCreatedBy(optional.get().getCreatedBy());

            return this.roleRepository.save(role);
        }
        return null;
    }

    public boolean isExistsRole(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Optional<Role> fetchRoleById(long id) {
        return this.roleRepository.findById(id);
    }

    public ResultPaginationDTO handleFetchAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(page.getContent());
        return rs;
    }

    public void handleDeleteRole(long id) {
        this.roleRepository.deleteById(id);
    }
}
