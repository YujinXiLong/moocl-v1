package com.mmall.vo;

import java.util.List;

public class CategoryVO {
	//private String url;
	
	//private boolean isPatent;
	
	private Integer id;

    private String name;
    
	private List<CategoryVO> childCategory;	
	
	
	public CategoryVO() {};
	public CategoryVO(Integer id, String name, List<CategoryVO> childCategory) {
		super();
		this.id = id;
		this.name = name;
		this.childCategory = childCategory;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CategoryVO> getChildCategory() {
		return childCategory;
	}

	public void setChildCategory(List<CategoryVO> childCategory) {
		this.childCategory = childCategory;
	}
	
}
