package fr.formation.jeuxolympique.repository;

import fr.formation.jeuxolympique.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository <Customer, Long> {

     Customer findByEmail(String email);

}
