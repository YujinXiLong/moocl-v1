package com.mmall.controller.potal;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;


//拦截问题
@Controller
@RequestMapping("/user/")
public class UserController {
	@Autowired
	private IUserService iUserService;
	
	
	
	//未登陆的拦截还没写
	@RequestMapping(value = "login.do" ,method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> login(String username,String password,HttpSession session){
		ServerResponse<User> serverResponse = iUserService.login(username, password);
		if (serverResponse.isSuccess()) {
			session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
			//return serverResponse;
		}
		
		//return ServerResponse.creatByError("登陆失败");
		return serverResponse;
	}
	
	//如果是我设计：session的key=username,value=user,退出登陆时，传一个username，delete user
	@RequestMapping(value = "logout.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> logout(HttpSession session){
		session.removeAttribute(Const.CURRENT_USER);
		return ServerResponse.creatBySuccess();
	}
	
	@RequestMapping(value = "register.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> register(User user){
		System.out.println(user.getPassword());
		ServerResponse<String> serverResponse = iUserService.register(user);
		return serverResponse;
	}
	//参数名不能随便起，要是str,type,不是的话就设置为null;
	@RequestMapping(value = "check_valid.do" ,method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> checkValid(String str,String type){
		return iUserService.checkValid(str, type);
	}
	
	@RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetGetQuestion(String username){
				return iUserService.getQuestionByUsername(username);
	}
	
	
	@RequestMapping(value = "forget_check_answer.do" ,method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetCheckAnser(String username,String question,String answer){
		return iUserService.checkAnswer(username, question, answer);
	}
	
	
		
	@RequestMapping(value = "forget_reset_password.do" ,method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> forgetRestPassword(String username,String newPassword,String token){
		return iUserService.forgetResetPassword(username, newPassword, token);
	}
	
	//老密码有什么用，既然是在线，根据userID直接修改就可以了,别人也不可能修改你的session的值。在网吧你可能要上厕所，别人用你的电脑直接修改你的密码，因为，是在线状态，有不用输入老密码，这样可以直接修改
	@RequestMapping(value = "reset_password.do" ,method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<String> restPassword(HttpSession session,String oldPassword,String newPassword){
			User user = (User) session.getAttribute(Const.CURRENT_USER);
			if (user == null) {
				return ServerResponse.creatByError("未登录");
			}
		return iUserService.resetPassword(user, oldPassword, newPassword);
	}
	
	
	@RequestMapping(value = "update_information.do" ,method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> updateInformation(User user,HttpSession session){
			User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
			if (currentUser == null) {
				return ServerResponse.creatByError("未登录");
			}
			user.setId(currentUser.getId());//防止越权
			user.setUsername(currentUser.getUsername());
			ServerResponse<User> result = iUserService.updateInformation(user);
			if (result.isSuccess()) {
				session.setAttribute(Const.CURRENT_USER, result.getData());
			}
		return result;
	}
	
	
	
	@RequestMapping(value = "get_user_info.do" ,method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<User> getUserInfo(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user!=null) {
			return ServerResponse.creatBySuccess(user);
		}
		return ServerResponse.creatByError("用户未登录,无法获取当前用户信息");
	}
	
	
		
		
	/*
	 * @RequestMapping(value = "get_information.do" ,method = RequestMethod.POST)
	 * 
	 * @ResponseBody public ServerResponse<User> getInformation(HttpSession
	 * session){ User user = (User) session.getAttribute(Const.CURRENT_USER); if
	 * (session.getAttribute(Const.CURRENT_USER)==null) { return
	 * ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),
	 * "用户未登录,无法获取当前用户信息,status=10,强制登录"); } return
	 * ServerResponse.creatBySuccess(user); }
	 */
	
	
	
}	

