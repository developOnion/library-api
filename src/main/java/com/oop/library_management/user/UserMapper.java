package com.oop.library_management.user;

import com.oop.library_management.common.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper extends BaseMapper<User, UserResponseDTO> {

	@Override
	public UserResponseDTO toDTO(User user) {

		if (user == null) {
			return null;
		}

		return new UserResponseDTO(
			user.getId(),
			user.getUsername(),
			user.getFirstName(),
			user.getLastName(),
			user.getRole(),
			user.getDisplayInfo()
		);
	}
}
