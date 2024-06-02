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

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create permission is success")
    public ResponseEntity<Permission> handleCreatePermission(@RequestBody Permission permission)
            throws IdInvalidException {
        if (this.permissionService.isExistsPermission(permission)) {
            throw new IdInvalidException("Permission đã tồn tại");
        }
        return ResponseEntity.ok().body(this.permissionService.handleCreatePermission(permission));
    }

    @PutMapping("permissions")
    @ApiMessage("update permission is success")
    public ResponseEntity<Permission> handleUpdatePermission(@RequestBody Permission permission)
            throws IdInvalidException {
        if (Long.valueOf(permission.getId()) == null
                || this.permissionService.fetchById(permission.getId()).isEmpty()) {
            if (this.permissionService.isSameName(permission)) {
                throw new IdInvalidException("Permission không tồn tại");
            }
        } else {
            if (this.permissionService.isExistsPermission(permission)) {
                throw new IdInvalidException("Permission đã tồn tại");
            }
            return ResponseEntity.ok().body(this.permissionService.handeleUpdatePermission(permission));
        }
        return null;
    }

    @GetMapping("permissions")
    @ApiMessage("fetch all permission")
    public ResponseEntity<ResultPaginationDTO> handleFetchAllPermission(
            @Filter Specification<Permission> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.permissionService.handleFetchAllPermission(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("delete permission is success")
    public ResponseEntity<Void> handleDeletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Permission> optional = this.permissionService.fetchById(id);
        if (optional.isEmpty()) {
            throw new IdInvalidException("permission không tồn tại");
        }
        this.permissionService.handleDeletePermission(id);
        return ResponseEntity.ok().body(null);
    }

}
