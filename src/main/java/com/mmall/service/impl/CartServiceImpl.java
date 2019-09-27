package com.mmall.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.JsonUtils;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;


@Service("iCartService")
public class CartServiceImpl implements ICartService {
	
	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;
	
	
	
	//横向越权，可以得到别的用户的，购物车?。
	public ServerResponse<CartVO> getCartProduct(Integer userId){
		return ServerResponse.creatBySuccess(this.getCartVOLimit(userId));
	}
		
	//传递产品以及数量，返回CartVO去渲染。
	public ServerResponse<?> add(Integer userId,Integer count,Integer productId) {
		//count<0
		if (count == null || productId == null||count < 0) {
			return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Product product = productMapper.selectByPrimaryKey(productId);
		//防止添加到购物车里的商品，的有效性          小心空指针，这里没处理，ON_SALE为Integer类型
		if (product == null || product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
			return ServerResponse.creatByError("抱歉该商品已经下架了");
		}
		Cart cart = cartMapper.selectByUserIdAndProductId(userId,productId);
		if (cart == null) {
			cart = new Cart();
			cart .setUserId(userId);
			cart.setProductId(productId);
			cart.setQuantity(count);
			cart.setChecked(Const.Cart.CHECKED);
			int countResult = cartMapper.insert(cart);
			if (countResult == 0) {
				return ServerResponse.creatByError("添加失败");
			}
		}
		Cart cartRecord = new Cart();
		cartRecord.setId(cart.getId());
		cartRecord.setQuantity(cart.getQuantity()+count);
		int countResult = cartMapper.updateByPrimaryKeySelective(cartRecord);
		if (countResult == 0) {
			
		}
		return ServerResponse.creatBySuccess(this.getCartVOLimit(userId));
	}
	
	//要对productIds做校验，不然报异常
	public ServerResponse<?> delete(Integer userId,String productIds){
		if (StringUtils.isBlank(productIds)) {
			return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		List<Integer> productIdList = JsonUtils.jsonToList(productIds, Integer.class);
		System.out.println(productIdList);
		int count =	cartMapper.deleteByUserIdAndProductIds(userId,productIdList);
		if (count == 0) {
			return ServerResponse.creatByError("删除失败");
		}
		return ServerResponse.creatBySuccess(this.getCartVOLimit(userId));
	}
	
	//修改的数量不能为负数
	public ServerResponse<?> updateQuantity(Integer userId,Integer productId,Integer count) {
		if (productId == null||count == null || count < 0) {
			return ServerResponse.creatByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
		}
		Cart cartRecord = new Cart();
			cartRecord.setUserId(userId);
			cartRecord.setProductId(productId);
			cartRecord.setQuantity(count);
		int countResult = cartMapper.updateByUserIdAndProductId(cartRecord);
		if (countResult == 0) {
			return ServerResponse.creatByError("更新产品Quantity失败");
		}
		return ServerResponse.creatBySuccess(this.getCartVOLimit(userId));
	}
	
	
	
	public ServerResponse<?> selectOrUnSelect(Integer userId,Integer productId ,Integer checked){
//		List<Cart> carts = cartMapper.selectByUserId(userId);
//		if (carts == null) {
//			return 
//		}
		int count = cartMapper.checkedOrUnchecked(userId,productId,checked);
		if (count == 0) {
			return ServerResponse.creatByError("更新勾选失败");
		}
		return ServerResponse.creatBySuccess(this.getCartVOLimit(userId));
	}
	
	public ServerResponse<?> getCartProductCount(Integer userId){
		return ServerResponse.creatBySuccess(cartMapper.selectCartProductCount(userId));
	}
	
	
	
	//找到该用户         将该用户的购物车里的所有商品，转换为CartProductVO,装到CartVO里面。返回到前台渲染。
	private CartVO getCartVOLimit(Integer userId) {
		CartVO cartVO = new CartVO();
		List<CartProductVO> cartProductVOList = new ArrayList<CartProductVO>();
		BigDecimal cartTotalPrice = new BigDecimal("0");
		List<Cart> cartList = cartMapper.selectByUserId(userId);
		if (!cartList.isEmpty()) {
			for(Cart cart : cartList) {
				CartProductVO cartProductVO = new CartProductVO();
				//BigDecimal productTotalPtice = new BigDecimal("0");
				cartProductVO.setId(cart.getId());
				cartProductVO.setUserId(cart.getUserId());
				cartProductVO.setProductId(cart.getProductId());
				
//				cartProductVO.setProductChecked(cart.getChecked());
//				
//				Product product = productMapper.selectByPrimaryKey(cart.getProductId());
//				if (product != null) {
//					cartProductVO.setProductName(product.getName());
//					cartProductVO.setProductSubtitle(product.getSubtitle());
//					cartProductVO.setProductMainImage(product.getMainImage());
//					cartProductVO.setProductStatus(product.getStatus());
//					cartProductVO.setProductStock(product.getStock());
//					int Stock = cart.getQuantity();
//					if (cart.getQuantity()>product.getStock()) {
//						Stock = product.getStock();
//						Cart cartRecord = new Cart();
//						cartRecord.setId(cart.getId());
//						cartRecord.setQuantity(product.getStock());
//						int count = cartMapper.updateByPrimaryKeySelective(cartRecord);
//						if (count == 0) {
//							System.out.println("更新不成功怎么办");
//						}
//						cartProductVO.setQuantity(Stock);
//						cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
//						cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
//					}
//					//购物车里的产品数量<库存时
//					cartProductVO.setQuantity(Stock);
//					cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
//					cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCES);
//				}
				//product == null时;
				
				Product product = productMapper.selectByPrimaryKey(cart.getProductId());
				if (product != null) {
					cartProductVO.setProductName(product.getName());
					cartProductVO.setProductSubtitle(product.getSubtitle());
					cartProductVO.setProductMainImage(product.getMainImage());
					cartProductVO.setProductStatus(product.getStatus());
					cartProductVO.setProductStock(product.getStock());
					cartProductVO.setPrice(product.getPrice());
					int Stock = 0;
					if (product.getStock()>=cart.getQuantity()) {
						Stock = cart.getQuantity();
						cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCES);
					}else {
						Stock = product.getStock();
						Cart cartRecord = new Cart();
						cartRecord.setId(cart.getId());
						cartRecord.setQuantity(product.getStock());
						int count = cartMapper.updateByPrimaryKeySelective(cartRecord);
						if (count == 0) {
							System.out.println("更新不成功怎么办");
						}
						cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
					}
					cartProductVO.setProductChecked(cart.getChecked());
					cartProductVO.setQuantity(Stock);
					cartProductVO.setProductTotalPrice(BigDecimalUtil.mul(cartProductVO.getPrice().doubleValue(),cartProductVO.getQuantity()));
				}
				//product == null时;不要返回过多的信息
				//System.out.println(null==1);
				if (cartProductVO.getProductChecked()==Const.Cart.CHECKED) {
					cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVO.getProductTotalPrice().doubleValue());
				}
				cartProductVOList.add(cartProductVO);
			}
		}
		//购物车为空时 即cartList为空。cartProductVoList:[]
		cartVO.setCartProductVoList(cartProductVOList);
		cartVO.setCartTotalPrice(cartTotalPrice);
		//遍历时购物车时，可以得到结果
		cartVO.setAllChecked(getAllCheckedStatus(userId));
		cartVO.setImageURI(PropertiesUtil.getProperty("FTP_IMAGEURL"));
		return cartVO;
	}
	
	//购物车为空(没有一件商品时)的时候也为全选，AllChecked为全选
	private boolean getAllCheckedStatus(Integer userId) {
		if (userId == null) {
			return false;
		}
		return cartMapper.AllCheckedStatus(userId)==0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
