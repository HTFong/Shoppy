package com.shoppy.admin.user;

import com.shoppy.common.entity.Role;
import com.shoppy.common.entity.User;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUser() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User user = new User("admin", "admin", "ADMIN", "ADMIN");
        user.getRoles().add(roleAdmin);
        User newRec = userRepo.save(user);
        assertThat(newRec.getId()).isGreaterThan(0);
    }
    @Test
    public void testCreateUserWithTwoRoles() {
        User user = new User("admin1", "admin1", "ADMIN1", "ADMIN1");
        Role roleAdmin = new Role();
        roleAdmin.setId(1);
        Role roleEditor = new Role();
        roleEditor.setId(3);
        user.getRoles().addAll(List.of(roleAdmin,roleEditor));
        User newRec = userRepo.save(user);
        assertThat(newRec.getId()).isGreaterThan(0);
    }
    @Test
    public void testGetAll() {
        userRepo.findAll().forEach(user -> System.out.println(user));
    }
    @Test
    public void testFindById() {
        System.out.println(userRepo.findById(3).get());
    }
    @Test
    public void testUpdateById() {
        User user = userRepo.findById(3).get();
        user.setFirstName("update");
        user.setLastName("update");
        userRepo.save(user);
    }
    @Test
    public void testDeleteById() {
        userRepo.deleteById(3);
    }
    @Test
    public void testFindByEmail() {
        assertThat(userRepo.findUserByEmail("user03@gmail.com")).isNotNull();
    }
    @Test
    public void testCountById() {
        assertThat(userRepo.countById(3)).isGreaterThan(0);
    }
    @Test
    public void testChangeEnabledStatus() {
        userRepo.updateEnabledStatus(true,3);
        userRepo.updateEnabledStatus(false,4);
    }
}
