package com.shoppy.admin.user;

import com.shoppy.common.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends PagingAndSortingRepository<User, Integer>, CrudRepository<User,Integer> {
    @Query("select u from User u where upper(u.email) = upper(:email)")
    User findUserByEmail(@Param("email") String email);

    Long countById(Integer id);

    @Query("update User u set u.enabled = ?1 where u.id = ?2")
    @Modifying
    void updateEnabledStatus(boolean enabled, Integer id);

}
