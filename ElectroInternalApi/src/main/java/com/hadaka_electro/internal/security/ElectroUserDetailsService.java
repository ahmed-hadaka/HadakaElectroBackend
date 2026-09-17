package com.hadaka_electro.internal.security;

import java.util.Optional;

import com.hadaka_electro.common.exception.ObjectNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hadaka_electro.internal.user.repository.UserRepository;
import com.hadaka_electro.common.entities.User;

@Service
@Transactional(readOnly = true)
public class ElectroUserDetailsService implements UserDetailsService {

	private UserRepository userRepository;

	public ElectroUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.findByEmail(username);
		if (user.isPresent()) {
			return new ElectroUserDetails(user.get());
		}
		throw new ObjectNotFoundException("There is no user with this email: " + username);
	}

}
