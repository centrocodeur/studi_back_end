package fr.formation.jeuxolympique;

import fr.formation.jeuxolympique.entity.Customer;
import fr.formation.jeuxolympique.entity.UserRole;
import fr.formation.jeuxolympique.repository.CustomerRepository;
import fr.formation.jeuxolympique.repository.UserRoleRepository;
import fr.formation.jeuxolympique.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class JeuxOlympiqueApplication {


	private AccountService accountService;
	private CustomerRepository customerRepository;
	private UserRoleRepository userRoleRepository;

	public JeuxOlympiqueApplication(CustomerRepository customerRepository, UserRoleRepository userRoleRepository) {
		this.customerRepository = customerRepository;
		this.userRoleRepository = userRoleRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(JeuxOlympiqueApplication.class, args);

	}



        @Bean
		CommandLineRunner star(AccountService accountService){
		return args -> {
			if(!userRoleRepository.existsById(1L)&& !userRoleRepository.existsById(2L) && !userRoleRepository.existsById(3L) ) {
				accountService.addNewRole(new UserRole(null, "USER"));
				accountService.addNewRole(new UserRole(null, "ADMIN"));
				accountService.addNewRole(new UserRole(null, "CUSTOMER_MANAGER"));
			} else { }

			if (!customerRepository.existsById(1l)) {

			accountService.addNewUser(new Customer(null,"admin","admin","admin@gmail.com","1234", true,new ArrayList<>()));
			//accountService.addNewUser(new Customer(null,"Rodrigue","Tapande","rodrigue@gmail.com","1234", false,new ArrayList<>()));

			accountService.addRoleToUser("admin@gmail.com","USER");
			accountService.addRoleToUser("admin@gmail.com","ADMIN");
			}
		};
	}


   }




