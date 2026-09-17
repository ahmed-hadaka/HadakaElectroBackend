package com.hadaka_electro.internal.user;

import com.hadaka_electro.common.entities.Role;
import com.hadaka_electro.common.entities.User;
import com.hadaka_electro.internal.user.repository.RoleRepository;
import com.hadaka_electro.internal.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserMapper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserMapper(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public UserDTO toDTO(User user) {

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEnabled(),
                user.getPhoto(),
                user.getRoles()
        );
    }

    public User toEntity(UserDTO userDTO) {
        int id = userDTO.getId();
        User user = userRepository.findById(id).orElse(new User());
        if(id <= 0){ //new
            user.setEmail(userDTO.getEmail());
        }
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEnabled(userDTO.isEnabled());
        if(id > 0){
            if(userDTO.getPhoto() != null && !userDTO.getPhoto().isEmpty())
                user.setPhoto(userDTO.getPhoto());
            if(userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }else{
            user.setPhoto(userDTO.getPhoto());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        user.setRoles(userDTO.getRoles());

        return user;
    }
}
