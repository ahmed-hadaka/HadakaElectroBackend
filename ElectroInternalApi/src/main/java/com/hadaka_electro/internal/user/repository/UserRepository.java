package com.hadaka_electro.internal.user.repository;

import com.hadaka_electro.common.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Long countById(int id);

    boolean existsByEmail(String email);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    void updateEnableStatus(int id, boolean isEnabled);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id,' ',u.firstName,' ',u.lastName,' ', u.email) LIKE %:keyword%")
    Page<User> findAll(String keyword, Pageable pageable);
}
