package fr.formation.jeuxolympique.service.Impl;

import fr.formation.jeuxolympique.entity.Customer;
import fr.formation.jeuxolympique.entity.UserRole;
import fr.formation.jeuxolympique.repository.CustomerRepository;
import fr.formation.jeuxolympique.repository.UserRoleRepository;
import fr.formation.jeuxolympique.service.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private CustomerRepository customerRepository;
    private UserRoleRepository userRoleRepository;

    private PasswordEncoder passwordEncoder;

    public AccountServiceImpl(CustomerRepository customerRepository, UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Customer addNewUser(Customer customer) {
        String pwd = customer.getPassword();
        customer.setPassword(passwordEncoder.encode(pwd));
        return customerRepository.save(customer);
    }

    @Override
    public UserRole addNewRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Override
    public void addRoleToUser(String email, String roleName) {

        Customer customer = customerRepository.findByEmail(email);
        UserRole userRole  = userRoleRepository.findByRoleName(roleName);
        customer.getUserRoles().add(userRole);
    }

    @Override
    public Customer loadUserByUsername(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override

    public List<Customer> listUsers() {
        return customerRepository.findAll();
    }
}
