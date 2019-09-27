package com.mmall.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import com.mmall.vo.CategoryVO;
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	public ServerResponse<List<Category>> getChildCategory(Integer parentId) {
		System.out.println(parentId);
		List<Category> categories = new ArrayList<Category>();
			categories = categoryMapper.selectCategoryByParentId(parentId);
			if (categories.isEmpty()) {
				return ServerResponse.creatByError("未找到该分类的子分类");
			}
		return ServerResponse.creatBySuccess(categories);
	}

	@Override
	public ServerResponse<String> addCategory(Integer parentId, String categoryName) {
		if (parentId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.creatByError("参数有误");
		}
		Category category = new Category();
				category.setParentId(parentId);
				category.setName(categoryName);
				category.setStatus(true);
			int count =	categoryMapper.insert(category);
		if (count == 0) {
			return ServerResponse.creatByError("添加失败");
		}
		return ServerResponse.creatBySuccess("添加成功");
	}

	//在controller中接受参数时，未用注解对parentId指定为0，同时传递的参数名有错误，那么会将parentId=null;
	@Override
	public ServerResponse<String> updateCategoryName(Integer categoryId, String categoryName) {
		if (categoryId == null || StringUtils.isBlank(categoryName)) {
			return ServerResponse.creatByError("参数有误");
		}
		Category category = new Category();
				category.setId(categoryId);
				category.setName(categoryName);
		int count = categoryMapper.updateByPrimaryKeySelective(category);
		if (count == 0) {
			return ServerResponse.creatByError("添加失败");
		}
		return ServerResponse.creatBySuccess("修改成功");
	}

	//要重写hash和eques实现排重
	public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
		if (categoryId == null) {
			return ServerResponse.creatByError("参数传递错误");
		}
	        Set<Category> categorySet = new HashSet<>();
	        findChildCategory(categorySet,categoryId);
	        List<Integer> categoryIdList = new ArrayList<Integer>();
	        if(categoryId != null){
	            for(Category categoryItem : categorySet){
	                categoryIdList.add(categoryItem.getId());
	            }
	        }
	        return ServerResponse.creatBySuccess(categoryIdList);
	    }


  	private Set<Category> findChildCategory(Set<Category> categorySet ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        List<Category> categoryList = categoryMapper.selectCategoryByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
		
	
	//要想形成树状结构：Category必须包含一个List类型的字段
	@Override
	public ServerResponse<List<CategoryVO>> getAllChildCategory(Integer categoryId) {
		List<Category> categories = categoryMapper.selectCategoryByParentId(categoryId);
		if (categories.isEmpty()) {
			ServerResponse.creatByError("没有该分类");
		}
		List<CategoryVO> categoryVOs = transToCategoryListVo(categories);
		return ServerResponse.creatBySuccess(categoryVOs);
	}
	
	
	private List<CategoryVO> transToCategoryListVo(List<Category> categories) {
		
		List<CategoryVO> categoryVOs = new ArrayList<CategoryVO>();
		//categories==null不会循环。
		for(Category category:categories) {
			CategoryVO categoryVO = new CategoryVO();
			categoryVO.setId(category.getId());
			categoryVO.setName(category.getName());
			
			List<Category> categorieList = categoryMapper.selectCategoryByParentId(category.getId());
			if (!categorieList.isEmpty()) {
				categoryVO.setChildCategory(transToCategoryListVo(categorieList));
			}else {
				categoryVOs.add(categoryVO);
			}
		}
		return categoryVOs;
	}
	
	
}
