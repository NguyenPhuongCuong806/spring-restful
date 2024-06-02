package vn.hoidanit.jobhunter.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.CreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.FindUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.UpdateUserDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<CreateUserDTO> createNewUser(@RequestBody @Valid User postmanUser) throws IdInvalidException {

        boolean isEmailExist = this.userService.isEmailExist(postmanUser.getEmail());

        if (isEmailExist) {
            throw new IdInvalidException("Email" + postmanUser.getEmail() + "đã tồn tại, vui lòng sử dụng email khác");
        }

        User user = this.userService.handleCreateUser(postmanUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertResCreateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User user = this.userService.handleFetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<FindUserDTO> fetchUserById(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.handleFetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("id không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertResFindUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleFetchAllUser(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("update user is success")
    public ResponseEntity<UpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(user.getEmail());
        User user2 = this.userService.handleFetchUserById(user.getId());
        if (user2 == null || !isEmailExist) {
            throw new IdInvalidException("id hoặc email không tồn tại");
        } else {
            User user3 = this.userService.handleUpdateUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertResUpdateUserDTO(user3));

        }

    }

}
