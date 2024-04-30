package fr.formation.jeuxolympique.security;


import fr.formation.jeuxolympique.entity.Customer;
import fr.formation.jeuxolympique.entity.UserRole;
import fr.formation.jeuxolympique.repository.CustomerRepository;
import fr.formation.jeuxolympique.security.filters.JwtAuthenticationFilter;
import fr.formation.jeuxolympique.security.filters.JwtAuthorizationFilter;
import fr.formation.jeuxolympique.service.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class ApplicationConfiguration implements UserDetailsService {


    private AccountService accountService;
    private CustomerRepository customerRepository;

    private final AuthenticationConfiguration authenticationConfiguration;

    public ApplicationConfiguration(AccountService accountService, CustomerRepository customerRepository, AuthenticationConfiguration authenticationConfiguration) {
        this.accountService = accountService;
        this.customerRepository = customerRepository;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception{

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .headers((headers)-> headers.disable())
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers("/refreshToken/**","/login/**").permitAll()
                                        .requestMatchers(HttpMethod.POST, "/users/**").hasAuthority("ADMIN")
                                        .requestMatchers(HttpMethod.GET, "/users/**").hasAuthority("USER")
                                        .anyRequest().authenticated()
                )
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //.formLogin(Customizer.withDefaults())
                //login
                .addFilter(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration)))
                // authorization of resources
                .addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();

   }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = accountService.loadUserByUsername(username);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for(UserRole userRole: customer.getUserRoles()){
            authorities.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        }

        return new User(customer.getEmail(), customer.getPassword(), authorities);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
