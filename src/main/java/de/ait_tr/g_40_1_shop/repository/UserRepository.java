package de.ait_tr.g_40_1_shop.repository;

import de.ait_tr.g_40_1_shop.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
   Optional<User>  findByUsername(String username);

}
