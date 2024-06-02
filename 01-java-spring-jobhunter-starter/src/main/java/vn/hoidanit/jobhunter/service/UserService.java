package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.CreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.FindUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.UpdateUserDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyService companyService,
            RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        if (!this.userRepository.existsByEmail(user.getEmail())) {
            // user.setPassword(Passwor);
            String hashPassWord = this.passwordEncoder.encode(user.getPassword());
            user.setPassword(hashPassWord);
            if (user.getCompany() != null) {
                Optional<Company> company = this.companyService.handleFetchCompanyById(user.getCompany().getId());
                user.setCompany(company.isEmpty() ? null : company.get());
            }

            if (user.getRole() != null) {
                Optional<Role> optional = this.roleService.fetchRoleById(user.getRole().getId());
                user.setRole(optional.isEmpty() ? null : optional.get());
            }

            return this.userRepository.save(user);
        }
        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleFetchUserById(long id) {
        if (!this.userRepository.findById(id).isEmpty())
            return this.userRepository.findById(id).get();
        return null;
    }

    public ResultPaginationDTO handleFetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);

        List<FindUserDTO> users = page.getContent()
                .stream().map(x -> this.convertResFindUserDTO(x))
                .collect(Collectors.toList());

        rs.setResult(users);

        return rs;
    }

    public User handleUpdateUser(User user) {
        Optional<User> optional = this.userRepository.findById(user.getId());
        if (this.userRepository.existsByEmail(user.getEmail())) {
            optional.get().setAddress(user.getAddress());
            optional.get().setAge(user.getAge());
            optional.get().setName(user.getName());
            optional.get().setGender(user.getGender());
            if (user.getCompany() != null) {
                Optional<Company> company = this.companyService.handleFetchCompanyById(user.getCompany().getId());
                if (company.isEmpty()) {
                    optional.get().setCompany(null);
                } else {
                    optional.get().setCompany(user.getCompany());

                }
            }

            if (user.getRole() != null) {
                Optional<Role> optionalRole = this.roleService.fetchRoleById(user.getRole().getId());
                optional.get().setRole(optionalRole.isEmpty() ? null : optionalRole.get());
            }

            return this.userRepository.save(optional.get());
        }
        return null;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public CreateUserDTO convertResCreateUserDTO(User user) {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setName(user.getName());
        createUserDTO.setEmail(user.getEmail());
        createUserDTO.setAddress(user.getAddress());
        createUserDTO.setAge(user.getAge());
        createUserDTO.setGender(user.getGender());
        if (user.getCompany() != null) {
            CreateUserDTO.Company company = new CreateUserDTO.Company();
            Optional<Company> company2 = this.companyService.handleFetchCompanyById(user.getCompany().getId());
            if (company2.isPresent()) {
                company.setName(company2.get().getName());
                company.setId(user.getCompany().getId());
                createUserDTO.setCompany(company);
            }
        }

        createUserDTO.setCreatedAt(user.getCreatedAt());
        return createUserDTO;
    }

    public UpdateUserDTO convertResUpdateUserDTO(User user) {
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setName(user.getName());
        updateUserDTO.setEmail(user.getEmail());
        updateUserDTO.setAddress(user.getAddress());
        updateUserDTO.setAge(user.getAge());
        updateUserDTO.setGender(user.getGender());
        updateUserDTO.setUpdatedAt(user.getUpdatedAt());
        if (user.getCompany() != null) {
            UpdateUserDTO.Company company = new UpdateUserDTO.Company();
            Optional<Company> company2 = this.companyService.handleFetchCompanyById(user.getCompany().getId());
            if (company2.isPresent()) {
                company.setName(company2.get().getName());
                company.setId(user.getCompany().getId());
                updateUserDTO.setCompany(company);
            }
        }
        return updateUserDTO;
    }

    public FindUserDTO convertResFindUserDTO(User user) {
        FindUserDTO findUserDTO = new FindUserDTO();
        findUserDTO.setId(user.getId());
        findUserDTO.setName(user.getName());
        findUserDTO.setEmail(user.getEmail());
        findUserDTO.setAddress(user.getAddress());
        findUserDTO.setAge(user.getAge());
        findUserDTO.setGender(user.getGender());

        if (user.getCompany() != null) {
            FindUserDTO.Company company = new FindUserDTO.Company();
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            findUserDTO.setCompany(company);
        }

        if (user.getRole() != null) {
            FindUserDTO.Role role = new FindUserDTO.Role();
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            findUserDTO.setRole(role);
        }

        findUserDTO.setCreatedAt(user.getCreatedAt());
        findUserDTO.setUpdatedAt(user.getUpdatedAt());

        return findUserDTO;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

}
