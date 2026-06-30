package com.classsystem.backend.mapper;

import com.classsystem.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM user WHERE username = #{username} AND role = #{role}")
    User findByUsernameAndRole(String username, String role);

    @Select("SELECT * FROM user WHERE role = #{role}")
    List<User> findByRole(String role);

    void insert(User user);

    void update(User user);
}