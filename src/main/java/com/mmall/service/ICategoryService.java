package com.mmall.service;

import java.util.List;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.vo.CategoryVO;

public interface ICategoryService {
	public ServerResponse<List<Category>> getChildCategory(Integer parentId);
	
	public ServerResponse<String> addCategory(Integer parentId, String categoryName);
	
	public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName);
	
	public ServerResponse<List<CategoryVO>> getAllChildCategory(Integer categoryId);
	
	public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
