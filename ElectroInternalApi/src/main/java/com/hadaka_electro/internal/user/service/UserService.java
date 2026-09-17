package com.hadaka_electro.internal.user.service;

import com.hadaka_electro.common.entities.User;
import com.hadaka_electro.common.exception.DuplicatedObjectException;
import com.hadaka_electro.common.exception.ObjectNotFoundException;
import com.hadaka_electro.internal.user.UserDTO;
import com.hadaka_electro.internal.user.UserMapper;
import com.hadaka_electro.internal.user.repository.UserRepository;
import com.hadaka_electro.internal.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public Page<UserDTO> listAllUsers(String keyword, Pageable pageable) {
        Page<User> users;
        if (keyword != null && !keyword.isEmpty()) {
            users = userRepository.findAll(keyword, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }
        return users.map(user -> {
            return userMapper.toDTO(user);
        });
    }


    @Transactional
    public void saveUser(UserDTO userDTO, MultipartFile multipartFile) throws IOException, DuplicatedObjectException {
        // In update: email can not be modified.
        User formUser = userMapper.toEntity(userDTO);
        String fileName = null;

        if (multipartFile != null && !multipartFile.isEmpty()) {
            fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            formUser.setPhoto(fileName);
        }

        User savedUser = userRepository.save(formUser);

        if (fileName != null) {
            String uploadDir = "user_photos/" + savedUser.getId();
            FileUtil.cleanDir(uploadDir);
            FileUtil.saveFile(uploadDir, fileName, multipartFile);
        }
    }


    public List<User> findAllSorted() {
        return userRepository.findAll(Sort.by("firstName").ascending());
    }

    public UserDTO findById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent())
            return userMapper.toDTO(user.get());
        throw new IllegalArgumentException("User not found with id: " + id);
    }

    public UserDTO findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return userMapper.toDTO(user.get());
        }
        throw new UsernameNotFoundException("User not found");
    }


    @Transactional
    public void deleteUser(int id) throws Exception, ObjectNotFoundException {
        long c = userRepository.countById(id);
        if (c > 0) {
            FileUtil.deletePhotosDir("user_photos/", id);
            userRepository.deleteById(id);
        } else {
            throw new ObjectNotFoundException("User not found with id: " + id);
        }
    }

    public long countById(int id) {
        return userRepository.countById(id);
    }

    @Transactional
    public boolean updateEnableStatus(int id) throws ObjectNotFoundException {
        Optional<User> user = userRepository.findById(id);
        boolean status = false;
        if (user.isPresent()) {
            status = user.get().isEnabled();
            userRepository.updateEnableStatus(id, !status);

        } else {
            throw new ObjectNotFoundException("User not found with id: " + id);
        }
        return !status;
    }

}
