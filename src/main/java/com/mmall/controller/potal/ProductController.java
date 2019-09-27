package com.mmall.controller.potal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;

@Controller
@RequestMapping("/product/")
public class ProductController {
	@Autowired
	private IProductService iProductService;
	
	
	//测试一下request,pageNum必须要填不然springMVC报错
	@RequestMapping(value = "list.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> list(@RequestParam("pageNum") Integer pageNum,
											@RequestParam("pageSize") Integer pageSize,
											String keyWord,Integer categoryIds,
											@RequestParam(value = "orderBy",defaultValue="")String orderBy,
											HttpSession session){
		return iProductService.playProductByKeyWordAndCategory(pageNum, pageSize, keyWord, categoryIds, orderBy);
	}
	
	
	
	
		
		
	@RequestMapping(value = "detail.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> detail(Integer productId,HttpSession session){
		return iProductService.playProductDetail(productId);
	}

	
}
