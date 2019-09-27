package com.mmall.controller.backend;


import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
//为什么用户模块的请求，都用post
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {
	@Autowired
	private ICategoryService iCategoryService;
	@Autowired
	private IUserService iUserService;

	
	//ServerResponse<List<Category>>不要这样用，因为返回的可能是ServerResponse<String>
	//有注解:不传parentId或者与传的参数与指定的parentId对不上，都默认传0，有注解，但是没加defaultValue = "0"，那么传递的参数名必须为parentId，还不能为空，否则报错
	//没注解：不传parentId那么parentId=null,传递错误参数paren，那么parentId=null;如果该请求为post请求，结果一样
	@RequestMapping(value = "get_category.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> getCategory(@RequestParam(value ="parentId",defaultValue = "0")Integer parentId,HttpSession session){ 
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还为登陆");
		}
		if (iUserService.checkAdminRole(user).isSuccess()) {
			return iCategoryService.getChildCategory(parentId);
		}
		return ServerResponse.creatByError("无权限");
	}
	
	@RequestMapping(value = "add_category.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<String> addCategory(@RequestParam(value = "parentId", defaultValue = "0") Integer parentId,String categoryName,HttpSession session){
		//System.out.println(parentId);//0
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还为登陆");
		}
		if (iUserService.checkAdminRole(user).isSuccess()) {
			return iCategoryService.addCategory(parentId,categoryName);
		}
		return ServerResponse.creatByError("无权限");
	}
	
	
	@RequestMapping(value = "set_category_name.do",method = RequestMethod.GET)
	@ResponseBody
	private ServerResponse<String> setCategoryName(Integer categoryId,String categoryName,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
			if (user == null) {
				return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还未登陆");
			}
			if (iUserService.checkAdminRole(user).isSuccess()) {
				return iCategoryService.updateCategoryName(categoryId, categoryName);
			}
		return ServerResponse.creatByError("无权限");
	}
	
	@RequestMapping(value = "get_deep_category.do",method = RequestMethod.GET)
	@ResponseBody
	private ServerResponse<?> getDeepCategory(Integer categoryId,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
			if (user == null) {
				return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还未登陆");
			}
			if (iUserService.checkAdminRole(user).isSuccess()) {
				return iCategoryService.selectCategoryAndChildrenById(categoryId);
			}
		return ServerResponse.creatByError("无权限");
	}
	
}
