package com.mmall.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import com.mmall.vo.ProductVO;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private CategoryMapper categoryMapper;
	@Autowired
	private ICategoryService iCategoryService;
	
	//包装类存在性能问题；这个地方要对orderBy（即字段+方式）有严格要求，不然是在数据库层面报错，现在还没加上去。
	
	@Override
	public ServerResponse<PageInfo<?>> productList(Integer pageNum,Integer pageSize,String orderBy) {
			if (pageNum==null || pageSize == null) {
				return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
			}
			if (StringUtils.isNoneBlank(orderBy)) {
				PageHelper.startPage(pageNum, pageSize,orderBy);
			}
			PageHelper.startPage(pageNum, pageSize);
			//将productMapper.selectAll()得到的集合的信息，设置给PageInfo对象
			List<Product> resultList = productMapper.selectAll();
			
			List<ProductVO> productVOList = new ArrayList<ProductVO>();
			//product==null，就不再循环了
			for(Product product : resultList) {
				productVOList.add(transToproducVotList(product));
			}
			//泛型如何解决
			PageInfo pageResult = new PageInfo<>(resultList);
			pageResult.setList(productVOList);
			
		return ServerResponse.creatBySuccess(pageResult);
	}
	
	private ProductVO transToproducVotList(Product product) {
		ProductVO productVo = new ProductVO();
		productVo.setId(product.getId());
		productVo.setCategoryId(product.getCategoryId());
		productVo.setName(product.getName());
		productVo.setSubtitle(product.getSubtitle());
		productVo.setPrice(product.getPrice());
		productVo.setStatus(product.getStatus());
		productVo.setMainImage(product.getMainImage());
		productVo.setSubImages(product.getSubImages());
		productVo.setImageURI(PropertiesUtil.getProperty("FTP_IMAGEURL"));
		return productVo;
	}
	
	//名字可以用模糊查询，现在还没用
	public ServerResponse<PageInfo<?>> searchProduct(Integer pageNum,Integer pageSize,String productName,Integer productId) {
		if (pageNum==null || pageSize == null) {
			return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		PageHelper.startPage(pageNum, pageSize);
		List<Product> resultList = productMapper.selectProductByNameOrID(productName,productId);
		List<ProductVO> productVOList = new ArrayList<ProductVO>();
		for(Product product : resultList) {
			productVOList.add(transToproducVotList(product));
		}
		PageInfo pageResult = new PageInfo<>(resultList);
		pageResult.setList(productVOList);
		return ServerResponse.creatBySuccess(pageResult);
		
	}

	@Override
	public ServerResponse<?> getProductDetail(Integer productId) {
			if (productId == null) {
				return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
			}
			Product product = productMapper.selectByPrimaryKey(productId);
			if (product == null) {
				return ServerResponse.creatByError("产品已下架或者删除");
			}
		return ServerResponse.creatBySuccess(transToProductDetailVO(product));
	}
	
	private ProductDetailVO transToProductDetailVO(Product product) {
		ProductDetailVO productDetailVO = new ProductDetailVO();
		productDetailVO.setId(product.getId());
		productDetailVO.setCategoryId(product.getCategoryId());
		productDetailVO.setName(product.getName());
		productDetailVO.setSubtitle(product.getSubtitle());
		productDetailVO.setPrice(product.getPrice());
		productDetailVO.setStatus(product.getStatus());
		productDetailVO.setMainImage(product.getMainImage());
		productDetailVO.setSubImages(product.getSubImages());
		productDetailVO.setStock(product.getStock());
		productDetailVO.setDetail(product.getDetail());
		//有什么用
		Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
		if (category==null) {
			productDetailVO.setParentCategoryId(0);
		}else {
			productDetailVO.setParentCategoryId(category.getParentId());
		}
		productDetailVO.setImageURI(PropertiesUtil.getProperty("FTP_IMAGEURL"));
		productDetailVO.setCreateTime(DateTimeUtil.DateToStr(product.getCreateTime()));
		productDetailVO.setUpdateTime(DateTimeUtil.DateToStr(product.getUpdateTime()));
		return productDetailVO;
	}
	
	//本来是要对status进行严格判断的，即只有三个值，1，2，3
	public ServerResponse<String> updateProductStatus(Integer productId,Integer status) {
		if (productId==null || status == null) {
			return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = new Product();
		product.setId(productId);
		product.setStatus(status);
		int count = productMapper.updateByPrimaryKeySelective(product);
		if (count >0) {
			return ServerResponse.creatBySuccessMsg("修改成功");
		}
		return ServerResponse.creatByError("修改失败");
	}


	@Override
	public ServerResponse<String> saveOrUpdate(Product product) {
			if (product == null || product.getId() == null) {
				return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
			}
			//减少传递的数据。因为MainImage可以不用传。
			if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
			Product result = productMapper.selectByPrimaryKey(product.getId());
			int count ;
			if (result==null) {
				Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
				if (category==null) {
					return ServerResponse.creatByError("产品所属的分类不存在");
				}
				count = productMapper.insert(product);
				if (count>0) {
					return ServerResponse.creatBySuccess();
				}
				return ServerResponse.creatByError("新增失败");
			}
			count = productMapper.updateByPrimaryKeySelective(product);
			if (count>0) {
				return ServerResponse.creatBySuccess();
			}
		return ServerResponse.creatByError("更新失败");
	}
	
	//=======================前台与后台最大的区别在于是否展示：保持上架状态的商品==========================================
	
	public ServerResponse<ProductDetailVO> playProductDetail(Integer productId){
			if (productId==null) {
				return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
			}
			Product product = productMapper.selectByPrimaryKey(productId);
			if (product==null) {
				return ServerResponse.creatByError("商品已经删除");
			}
			if (product.getStatus()==Const.ProductStatusEnum.OBTAINED.getCode()) {
				return ServerResponse.creatByError("商品已下架");
			}
		return ServerResponse.creatBySuccess(transToProductDetailVO(product));
	}
	
	
	public ServerResponse<PageInfo<?>> playProductByKeyWordAndCategory(Integer pageNum,Integer pageSize,String keyWord,Integer categoryId,String orderBy) {
			if (pageNum==null||pageSize==null) {
				return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
			}
			//ServerResponse<List<Integer>> responseResult;
			List<Integer> categoryIds = new ArrayList<Integer>();
			if (categoryId!=null) {
				PageHelper.startPage(pageNum, pageSize);
				Category category = categoryMapper.selectByPrimaryKey(categoryId);
				if (category==null&&StringUtils.isBlank(keyWord)) {
					PageHelper.startPage(pageNum, pageSize);
					List<Product> emptList= new ArrayList<Product>();
					PageInfo<Product> pageInfo = new PageInfo<Product>(emptList);
					return ServerResponse.creatBySuccess(pageInfo);
				}
				//responseResult = iCategoryService.selectCategoryAndChildrenById(categoryId);
				categoryIds = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
			}
			
			PageHelper.startPage(pageNum, pageSize);
			if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
				String dir[] = orderBy.split("_");
				orderBy = dir[0] + " " + dir[1];
				PageHelper.orderBy(orderBy);//会把orderBy的类容加到sql上，所以为了防止报错，要进行严格判断。
			}
			//keyWord=""则是查全部。
			if (StringUtils.isNotBlank(keyWord)) {
				keyWord = new StringBuffer().append("%").append(keyWord).append("%").toString();
			}
			//List<Product> productResult = productMapper.selectProductByKeyWordOrCategoryIds(keyWrod,responseResult.getData().isEmpty()?null:responseResult.getData());
			List<Product> productResult = productMapper.selectProductByKeyWordOrCategoryIds(StringUtils.isBlank(keyWord)?null:keyWord,categoryIds.size()==0?null:categoryIds);
			List<ProductVO> productVOs = new ArrayList<ProductVO>();
			for(Product product : productResult) {
				productVOs.add(transToproducVotList(product));
			}
			PageInfo pageInfo = new PageInfo(productResult);
			pageInfo.setList(productVOs);
			return ServerResponse.creatBySuccess(pageInfo);
	}
}
