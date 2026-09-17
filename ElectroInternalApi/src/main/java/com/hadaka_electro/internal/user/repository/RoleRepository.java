package com.hadaka_electro.internal.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hadaka_electro.common.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
