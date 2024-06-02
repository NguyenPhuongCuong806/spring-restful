package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission handeleUpdatePermission(Permission permission) {
        Optional<Permission> optional = this.fetchById(permission.getId());
        if (optional.isPresent()) {
            permission.setCreatedAt(optional.get().getCreatedAt());
            permission.setCreatedBy(optional.get().getCreatedBy());
            return this.permissionRepository.save(permission);
        }
        return null;
    }

    public boolean isExistsPermission(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                permission.getModule(), permission.getApiPath(), permission.getMethod());
    }

    public Optional<Permission> fetchById(long id) {
        return this.permissionRepository.findById(id);
    }

    public ResultPaginationDTO handleFetchAllPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> page = this.permissionRepository.findAll(spec, pageable);

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

    public void handleDeletePermission(long id) {
        Optional<Permission> optional = this.permissionRepository.findById(id);

        Permission permission = optional.get();
        permission.getRoles()
                .stream().forEach(x -> x.getPermissions().remove(permission));

        this.permissionRepository.delete(permission);
    }

    public boolean isSameName(Permission permission) {
        return this.permissionRepository.existsByName(permission.getName());
    }

}
