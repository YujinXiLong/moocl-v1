package com.mmall.controller.backend;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUploadService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
@RequestMapping("/manage/product/")
@Controller
public class ProductManageController {
	@Autowired
	private IProductService iProductService;
	@Autowired
	private IUserService iUserService;
	@Autowired
	private IUploadService iUploadService;

	
	
	//必须传递id,对不为空的字段要进行判断
	@RequestMapping(value = "save.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> save(Product product,HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user ==null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还为登陆");
		}
		if (iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.saveOrUpdate(product);
		}
		return ServerResponse.creatByError("你还不是管理员");
	}
	
	@RequestMapping(value = "list.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> getProductList(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user==null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还为登陆");
		}
		if (iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.productList(pageNum, pageSize, null);
		}
		return ServerResponse.creatByError("你还不是管理员");
	}
	
	@RequestMapping(value = "search.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> search(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
									@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
									String productName,Integer productId,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user==null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还未登陆，请登陆");
		}
		if (iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.searchProduct(pageNum, pageSize, productName, productId);
		}
		return ServerResponse.creatByError("你还不是管理员");
	}
	
	@RequestMapping(value = "detail.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> getDetail(Integer productId ,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user==null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还未登陆，请登陆");
		}
		if (iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService.getProductDetail(productId);
		}
		return ServerResponse.creatByError("你还不是管理员");
	}
	
	@RequestMapping(value = "set_sale_status.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> setSaleStatus(Integer productId ,Integer status ,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user==null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还未登陆，请登陆");
		}
		if (iUserService.checkAdminRole(user).isSuccess()) {
			return iProductService. updateProductStatus(productId,status);
		}
		return ServerResponse.creatByError("你还不是管理员");
	}
		
	@RequestMapping(value = "upload.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> upload(@RequestParam(value = "upload_file")MultipartFile file,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user==null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"您还未登陆，请登陆");
		}
		if (iUserService.checkAdminRole(user).isSuccess()) {
			ServerResponse<?> responseResult = iUploadService.upload(file);
			if (responseResult.isSuccess()) {
				Map<String, Object> data = new HashMap<String, Object>();
				String url = (String) responseResult.getData();
				data.put("url", url);
				String uri = StringUtils.remove(url ,PropertiesUtil.getProperty("FTP_IMAGEURL"));
				data.put("uri", uri);
				return ServerResponse.creatBySuccess(data);
			}
			return responseResult;
		}
		return ServerResponse.creatByError("你还不是管理员");
	}
		
	@RequestMapping(value = "richtext_img_upload.do",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> richtextImgUpload(@RequestParam(value = "upload_file")MultipartFile file,HttpSession session,HttpServletRequest request,HttpServletResponse response){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		Map<String, Object> map = new HashMap<String, Object>();
		if (user!=null) {
			if (iUserService.checkAdminRole(user).isSuccess()) {
				String temPath = request.getSession().getServletContext().getRealPath("upload");
				ServerResponse<?> responseResult = iUploadService.mutilUpload(file, temPath);
				if (responseResult.isSuccess()) {
					String url = (String) responseResult.getData();
					map.put("success",true);
					map.put("msg","上传成功");
					map.put("file_path",url);
		            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
					return map;
				}
			}
			map.put("success",false);
			map.put("msg","您没有权限");
			map.put("file_path",null);
			 response.addHeader("Access-Control-Allow-Headers","X-File-Name");
			return map;
		}
		map.put("success",false);
		map.put("msg","您还未登录，请登陆");
		map.put("file_path",null);
		 response.addHeader("Access-Control-Allow-Headers","X-File-Name");
		
		return map;
	}
}
