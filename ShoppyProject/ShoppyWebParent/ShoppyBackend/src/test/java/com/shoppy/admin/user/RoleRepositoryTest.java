package com.shoppy.admin.user;

import com.shoppy.common.entity.Role;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepo;

    @Test
    public void testCreateFirstRole() {
        Role newRec = roleRepo.save(new Role("Admin", "Manage everything"));
        assertThat(newRec.getId()).isGreaterThan(0);
    }
    @Test
    public void testCreateRestRoles() {
        Role admin = roleRepo.save(new Role("Admin", "Manage everything"));
        Role salesperson = roleRepo.save(new Role("Salesperson", "Manage product price, customer,shipping, orders and sales report"));
        Role editor = roleRepo.save(new Role("Editor", "Manage categories, bands, products, articles and menus"));
        Role shipper = roleRepo.save(new Role("Shipper", "View products, view orders and update order status"));
        Role assistant = roleRepo.save(new Role("Assistant", "Manage questions and reviews"));
        roleRepo.saveAll(List.of(admin, salesperson, editor, shipper, assistant));
    }
}
