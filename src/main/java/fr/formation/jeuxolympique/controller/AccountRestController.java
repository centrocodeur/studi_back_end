package fr.formation.jeuxolympique.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.formation.jeuxolympique.entity.Customer;
import fr.formation.jeuxolympique.entity.UserRole;
import fr.formation.jeuxolympique.security.JWTUtil;
import fr.formation.jeuxolympique.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AccountRestController {

    private AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/users")
    public List <Customer> customerList (){return accountService.listUsers();}

    @PostMapping(path = "/users")
    public Customer SaveUser(@RequestBody Customer customer){return accountService.addNewUser(customer);}

    @PostMapping(path = "/roles")
    public UserRole SaveRole(@RequestBody UserRole userRole){
        return accountService.addNewRole(userRole);
    }



    @PostMapping(path = "/AddRoleToUser")
    public  void addRoleToUser(@RequestBody RoleUseForm roleUseForm){
        accountService.addRoleToUser(roleUseForm.getUsername(), roleUseForm.getRoleName());

    }


    @GetMapping(path = "/profile")
    public Customer profile(Principal principal){  // principal = username

        return accountService.loadUserByUsername(principal.getName());
    }
    // Add Roles



    @GetMapping(path= "/refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authToken = request.getHeader(JWTUtil.AUTH_HEADER);

        if(authToken!=null && authToken.startsWith(JWTUtil.FREFIX)){
            try{
                String jwt = authToken.substring(JWTUtil.FREFIX.length());
                Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                String username = decodedJWT.getSubject();
                Customer customer= accountService.loadUserByUsername(username);

                String jwtAccessToken = JWT.create()
                        .withSubject(customer.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis()+JWTUtil.EXPIRE_ACCESS_TOKEN))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", customer.getUserRoles().stream().map(r->r.getRoleName()).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> idToken = new HashMap<>();
                idToken.put("access-token", jwtAccessToken);
                idToken.put("refresh-token", jwt);
                response.setContentType("application/json");

                new ObjectMapper().writeValue(response.getOutputStream(), idToken);
            } catch (Exception e){

                //response.setHeader("error-message", e.getMessage());
                //response.sendError(HttpServletResponse.SC_FORBIDDEN);
                throw e;
            }
        } else {
            throw new RuntimeException("Refresh token required");
        }

    }


}
