package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);
    
    int updateByPrimaryKey(User record);
    
    User selectLogin(@Param("username") String username, @Param("password") String password);
    
    int checkUsername(String username);
    
    int checkPassword(String password);
    
    int checkEmail(String email);
    
    List<String> selectQuestionByUsername(String username);
    
    //String selectAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);
    
    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);
    
    int updatePasswordByUsername(@Param("username") String username, @Param("newPassword") String newPassword);
    
    int updatePasswordByUserID(@Param("id") Integer id, @Param("newPassword") String newPassword);
    
    int checkPasswordByUserID(@Param("id") Integer id, @Param("oldPassword") String oldPassword);
    
    int checkEmailByUserID(@Param("email") String email, @Param("id") Integer id);
    
    int checkPhoneByUserID(@Param("phone") String phone, @Param("id") Integer id);
    
    int updateByPrimaryKeyEncludePassword(User record);
}