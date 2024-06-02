package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> page = this.companyRepository.findAll(spec, pageable);
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

    public Company handleUpdateCompany(Company company) {
        Optional<Company> optional = this.companyRepository.findById(company.getId());
        if (optional.isPresent()) {
            Company company2 = optional.get();
            company2.setLogo(company.getLogo());
            company2.setName(company.getName());
            company2.setDescription(company.getDescription());
            company2.setAddress(company.getAddress());
            return this.companyRepository.save(company2);

        }
        return null;
    }

    public void handleDeleteCompany(Long id) {
        Optional<Company> optional = this.companyRepository.findById(id);
        if (optional.isPresent()) {
            Company company = optional.get();

            List<User> users = this.userRepository.findByCompany(company);

            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> handleFetchCompanyById(long id) {
        return this.companyRepository.findById(id);
    }
}
