package fr.formation.jeuxolympique.service;

import fr.formation.jeuxolympique.entity.Customer;
import fr.formation.jeuxolympique.entity.UserRole;

import java.util.List;

public interface AccountService {

    Customer addNewUser (Customer customer);
    UserRole addNewRole (UserRole userRole);

    void addRoleToUser(String email, String roleName);

    Customer loadUserByUsername( String email);

    List<Customer> listUsers();
}


