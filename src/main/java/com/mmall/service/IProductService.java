package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVO;

public interface IProductService {
	public ServerResponse<PageInfo<?>> productList(Integer pageNum, Integer pageSize, String orderBy);
	
	public ServerResponse<PageInfo<?>> searchProduct(Integer pageNum, Integer pageSize, String productName, Integer productId);
	
	public ServerResponse<?>getProductDetail(Integer productId);
	
	public ServerResponse<String> updateProductStatus(Integer productId, Integer status);
	
	public ServerResponse<String> saveOrUpdate(Product product);
	
	public ServerResponse<ProductDetailVO> playProductDetail(Integer productId);
	
	public ServerResponse<PageInfo<?>> playProductByKeyWordAndCategory(Integer pageNum, Integer pageSize, String keyWord, Integer categoryId, String orderBy);
	
}
