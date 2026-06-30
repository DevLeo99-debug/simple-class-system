package com.classsystem.backend.mapper;

import com.classsystem.backend.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface NotificationMapper {

    @Select("SELECT * FROM notification WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Notification> findByUserId(Long userId);

    void insert(Notification notification);

    @Update("UPDATE notification SET is_read = 1 WHERE id = #{id}")
    void markAsRead(Long id);
}