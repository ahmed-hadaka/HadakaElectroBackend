package com.hadaka_electro.internal.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.hadaka_electro.common.entities.Role;
import com.hadaka_electro.common.entities.User;

//By default, @DataJpaTest wraps every test in a transaction and rolls it back when the test finishes.
@DataJpaTest
// do tests against real db (mysql)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TestUserRepository {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	private User user1;
	private User user2;
	private Role role1;
	private Role role2;
	private Role role3;

	@BeforeEach
	public void init() {
		User userWithOneRole = new User("ahmedtest1@gmail.com", "password", "ahmed", "hadaka");
		User userWithTwoRoles = new User("ahmedtest2@gmail.com", "password", "ahmed", "hadaka");
		role1 = new Role("roleName1", "roleDesc1");
		role2 = new Role("roleName2", "roleDesc2");
		role3 = new Role("roleName3", "roleDesc3");

		roleRepository.saveAll(List.of(role1, role2, role3));

		userWithOneRole.addRole(role1);
		userWithTwoRoles.addRole(role2);
		userWithTwoRoles.addRole(role3);

		user1 = userRepository.save(userWithOneRole);
		user2 = userRepository.save(userWithTwoRoles);
	}

	@Test
	public void testCreateUser() {
		assertThat(user1.getId()).isGreaterThan(0);
		assertThat(user2.getId()).isGreaterThan(0);

		assertEquals("ahmedtest1@gmail.com", user1.getEmail());
		assertEquals("ahmedtest2@gmail.com", user2.getEmail());

		assertEquals(1, user1.getRoles().size());
		assertEquals(2, user2.getRoles().size());
	}

	@Test
	public void testFindByEmail() {
		assertTrue(userRepository.findByEmail("ahmedtest1@gmail.com").isPresent());
	}

	@Test
	public void testListAllUsers() {
		List<User> users = userRepository.findAll();
		assertEquals(2, users.size());
	}

	@Test
	public void testUpdateUserDetails() {
		User user = userRepository.findByEmail("ahmedtest2@gmail.com")
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		user.setFirstName("AHMED");
		user.setEnabled(true);
		user.getRoles().clear();
		role2.setName("updatedRoleName");
		user.getRoles().add(role2);

		userRepository.save(user);

		assertEquals("AHMED", user.getFirstName());
		assertTrue(user.isEnabled());
		assertEquals("updatedRoleName", role2.getName());
		assertEquals(role2, user.getRoles().stream().findFirst().orElseThrow());
	}

	@Test
	public void testDeleteUser() {
		userRepository.delete(user1);
		assertTrue(userRepository.findByEmail("ahmedtest1@gmail.com").isEmpty());
	}

	@Test
	public void testSearchWithKeyword() {
		String keyword = "1";
		Pageable pageable = PageRequest.of(1, 1);
		Page<User> users = userRepository.findAll(keyword, pageable);

		assertThat(users.getTotalElements()).isGreaterThan(0);
		assertThat(users.getTotalElements()).isEqualTo(1);
	}

}