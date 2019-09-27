package com.mmall.service.impl;

import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;


//角色设置

@Service("iUserService")
public class UserServiceImpl implements IUserService {
	@Autowired
	private UserMapper userMapper;
	

	//用户名+密码，邮箱+密码，手机号+密码，扫二维码，手机短信验证登陆，第三方登陆
	//第一个参数万一是邮箱 又或者是手机号，该怎么办
	@Override
	public ServerResponse<User> login(String username,String password){
			int count = userMapper.checkUsername(username);
			if (count==0) {
				return ServerResponse.creatByError("用户名不存在");
			}
		/*
		 * String md5Password = MD5Util.MD5EncodeUtf8(password);
		 * count = userMapper.checkPassword(md5Password); 
		 * if (count == 0) { return
		 * ServerResponse.creatByError("密码错误"); }
		 */
			String md5Password = MD5Util.MD5EncodeUtf8(password);
			User user = userMapper.selectLogin(username, md5Password);
			if (user == null) {
				return ServerResponse.creatByError("密码错误");
			}
			user.setPassword(StringUtils.EMPTY);//EMPTY="",直接设置成""不好吗？
		return ServerResponse.creatBySuccess(user);
	}
	
	
	//用户名，手机号，邮箱，只能有一个，所以要校验提示，我的没有手机验证--username,password,email,phone,question,answer
	@Override
	public ServerResponse<String> register(User user) {
		int count = userMapper.checkUsername(user.getUsername());
		if (count > 0) {
			return ServerResponse.creatByError("用户名已存在");
		}
		count = userMapper.checkEmail(user.getEmail());
		if (count > 0) {
			return ServerResponse.creatByError("该邮箱已经注册过了");
		}
		user.setRole(Const.Role.Role_CUSTEMER);
		if (StringUtils.isBlank(user.getPassword())){
			return ServerResponse.creatByError("密码不能为空");
		}
		user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
		count = userMapper.insert(user);
		if (count == 0 ) {
			return ServerResponse.creatByError("注册失败");
		}
		return ServerResponse.creatBySuccess("注册成功");
	}


	@Override
	public ServerResponse<String> checkValid(String str, String type) {
		System.out.println(str);
		System.out.println(type);
		if (StringUtils.isNotBlank(type)) {
			int count;
			if (Const.USERNAME.equals(type)) {
				count = userMapper.checkUsername(str);
				if (count > 0) {
					return ServerResponse.creatByError("用户名已存在");
				}
			}
			if (Const.EMAIL.equals(type)) {
				count = userMapper.checkEmail(str);
				if (count > 0) {
					return ServerResponse.creatByError("该邮箱已存在");
				}
			}
		}else {
			return ServerResponse.creatByError("参数错误");
		}
		return ServerResponse.creatBySuccess("校验成功");
	}
								
	//请您输入用户名/邮箱/手机                        
	@Override				   
	public ServerResponse<String> getQuestionByUsername(String username) {
		int count = userMapper.checkUsername(username);
		if (count==0) {
			return ServerResponse.creatByError("用户名不存在");
		}
		List<String> questions = userMapper.selectQuestionByUsername(username);
		
		  if (questions.isEmpty()) {//问题可能为"" ，默认null,或者传递参数为null,select user_name from user where user_password = null;结果为空集
			  return ServerResponse.creatByError("你还未设置问题"); 
		  }
			if (StringUtils.isBlank(questions.get(0))) {
					return ServerResponse.creatByError("问题为空");
			}
		
		return ServerResponse.creatBySuccess(questions.get(0));//当list里面没有一个元素时，get(0)会报错，
	}

											//postman
	//问题找回密码，手机验证找回密码					//answer=答案&此时question的值为null
											//question=&answer=答案此时的question的值为""
	@Override							   
	public ServerResponse<String> checkAnswer(String username, String question, String answer) {
//		String currentanswer = userMapper.selectAnswer(username, question);
//			//currentanswer要判断为不为null
//			if (currentanswer.equals(answer)) {
//				String token = UUID.randomUUID().toString();
//				TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,token);
//				return ServerResponse.creatBySuccess(token);
//			}
		System.out.println(question);
		int count = userMapper.checkAnswer(username, question, answer);
		if (count > 0) {
			String token = UUID.randomUUID().toString();
			TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,token);
			return ServerResponse.creatBySuccess(token);
		}
		return ServerResponse.creatByError("答案错误");
	}

	
	@Override
	public ServerResponse<String> forgetResetPassword(String username, String newPassword, String token) {
		if (StringUtils.isBlank(token)) {
			return ServerResponse.creatByError("还未传递token");
		}
		if (userMapper.checkUsername(username)==0) {
			return ServerResponse.creatByError("用户不存在");
		}
		String currentToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
		if (StringUtils.equals(currentToken, token)) {//null==null ture
			String MD5newPassword = MD5Util.MD5EncodeUtf8(newPassword);
			int count = userMapper.updatePasswordByUsername(username, MD5newPassword);
			if (count>0) {
				return ServerResponse.creatBySuccessMsg("密码重置成功");
			}else {
				return ServerResponse.creatByError("密码重置失败");
			}
		}
		return ServerResponse.creatByError("token已过期，请重新回答问题");
	}

	
	public ServerResponse<String> resetPassword(User user,String oldPassword,String newPassword) {
			String MD5oldPassword = MD5Util.MD5EncodeUtf8(oldPassword);
			int count = userMapper.checkPasswordByUserID(user.getId(), MD5oldPassword);
			System.out.println(user.getId());
			System.out.println(count);
			if (count == 0) {
				return ServerResponse.creatByError("旧密码错误");
			}
			String MD5newPassword = MD5Util.MD5EncodeUtf8(newPassword);
			count = userMapper.updatePasswordByUsername(user.getUsername(), MD5newPassword);
			if (count == 0) {
				return ServerResponse.creatByError("密码修改失败");
			}
		return ServerResponse.creatBySuccess("密码修改成功");
	}

	//username不能被更新，为什么？ Email phone 修改时要校验，是否被他人占用
	@Override
	public ServerResponse<User> updateInformation(User user) {
		int count = userMapper.checkEmailByUserID(user.getEmail(),user.getId());
		if (count>1) {
			return ServerResponse.creatByError("email 已经存在 ！ 请尝试新的email");
		}
			count = userMapper.checkPhoneByUserID(user.getPhone(), user.getId());
		if (count>1) {
			return ServerResponse.creatByError("phone 已经注册过了！ 请尝试新的phone");
		}
		
		
			count = userMapper.updateByPrimaryKeyEncludePassword(user);
		if (count == 0) {
			return ServerResponse.creatByError("更新失败");
		}
		
		user = userMapper.selectByPrimaryKey(user.getId());
		user.setPassword(StringUtils.EMPTY);
		return ServerResponse.creatBySuccess(user);

//		User updateUser = new User();
//		updateUser.setId(user.getId());
//		updateUser.setUsername(user.getUsername());
//		updateUser.setEmail(user.getEmail());
//		updateUser.setPhone(user.getPhone());
//		updateUser.setQuestion(user.getQuestion());
//		if (StringUtils.isBlank(user.getQuestion())) {
//			return ServerResponse.creatByError("问题不能为空");
//		}
//		System.out.println(user.getId());
//		updateUser.setAnswer(user.getAnswer());
//		count = userMapper.updateByPrimaryKeySelective(updateUser);
//		if (count == 0) {
//			return ServerResponse.creatByError("信息更新失败");
//		}
//		
//		return ServerResponse.creatBySuccess(updateUser);
	}
	
	
	//责任分离做到，controllser不要做服务
	public ServerResponse<String> checkAdminRole(User user){
		if (user.getRole().intValue() != Const.Role.Role_ADMIN) {
			return ServerResponse.creatByError("您还不是管理员");
		}
		return ServerResponse.creatBySuccess();
		
	}
	
	

	
	
	
	
	
	
	
	
//	private int checkUsername(String username) {
//		return 0;
//		
//	}
//	private int checkPasswordByUsername(String password,String username) {
//		return 0;
//	} 
//	private int checkPasswordByUserID(String password,Integer id) {
//		return 0;
//		
//	} 
//	private int checkEmailByUsername(String email,String username) {
//		return 0;
//		
//	} 
//	private int checkEmailByUserID(String email,Integer id) {
//		return 0;
//		
//	} 
//	private int checkPhoneByUsername(String phone,String username) {
//		return 0;
//		
//	} 
//	private int checkPhoneByUserID(String phone,String id) {
//		return 0;
//	} 


}
