package com.coin.mapper;

import com.coin.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName UserMapper
 * @Description: TODO
 * @Author kh
 * @Date 2020-09-04 8:27
 * @Version V1.0
 **/
public interface UserMapper {
    List<User> selectUserByUsername(String username);

    List<User> selectUserById(String id);

    List<User> searchForUser(@Param("firstResult") int firstResult, @Param("maxResults") int maxResults);
}
