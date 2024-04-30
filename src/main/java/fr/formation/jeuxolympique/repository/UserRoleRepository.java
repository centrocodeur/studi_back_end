package fr.formation.jeuxolympique.repository;

import fr.formation.jeuxolympique.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository  extends JpaRepository <UserRole, Long> {
    UserRole findByRoleName(String roleName);
}
