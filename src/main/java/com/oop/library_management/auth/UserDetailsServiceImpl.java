package com.oop.library_management.auth;

import com.oop.library_management.user.User;
import com.oop.library_management.user.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(
		UserRepository userRepository
	) {
		this.userRepository = userRepository;
	}

	@Override
	@NullMarked
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username)
		throws UsernameNotFoundException {

		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new UserPrincipal(user);
	}
}
