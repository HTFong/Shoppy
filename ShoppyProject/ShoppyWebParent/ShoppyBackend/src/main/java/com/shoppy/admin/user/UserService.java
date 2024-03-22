package com.shoppy.admin.user;

import com.shoppy.common.entity.Role;
import com.shoppy.common.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll() {
        return (List<User>) userRepo.findAll();
    }

    public Page<User> listAllByPage(int pageNumber, int pageSize, String sortField, String sortDirect, String keyword) {
        Sort sort = sortDirect.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1,pageSize,sort);

        return keyword!=null
                ? userRepo.findByKeyword(keyword,pageable)
                : userRepo.findAll(pageable);
    }

    public List<Role> listRoles() {
        return  (List<Role>) roleRepo.findAll();
    }
    public User save(User user) {
        boolean isUpdatingUser = user.getId() != null;
        if (isUpdatingUser) {
            User existingUser = userRepo.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }else {
                encodePassword(user);
            }
        }else {
            encodePassword(user);
        }
        return userRepo.save(user);
    }
    public void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepo.findUserByEmail(email);

        if(userByEmail != null) return true;

        boolean isNewRecord = (id == null || id == 0);
        if (isNewRecord) {
            if (userByEmail != null) return false;
        }else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }
        return true;
    }

    public User getById(Integer id) throws UserNotFoundException {
        try {
            return userRepo.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new UserNotFoundException("Could not find any user with ID:" + id);
        }
    }

    public void delete(Integer id) throws UserNotFoundException {
        Long countUser = userRepo.countById(id);
        if (countUser == null || countUser == 0) {
            throw new UserNotFoundException("Could not find any user with ID: " + id);
        }
        userRepo.deleteById(id);
    }

    public void changeEnabled(boolean enabled, Integer id) {
        userRepo.updateEnabledStatus(enabled,id);
    }
}