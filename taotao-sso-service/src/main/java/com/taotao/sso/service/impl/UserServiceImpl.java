package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.taotao.common.utils.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.UserService;

/**
 * 用户处理Service
 * @author Administrator
 *
 */
public class UserServiceImpl implements UserService{

	@Autowired
	private TbUserMapper userMapper;
	
	@Override
	public TaotaoResult checkDate(String data, Integer type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		// 设置查询条件
		// 1 判断用户名是否可用 username  
		if (type == 1) {
			criteria.andUsernameEqualTo(data);
		// 2 判断手机号是否可用
		}else if(type == 2){
			criteria.andPhoneEqualTo(data);
		// 3 判断邮箱是否可用
		}else if(type == 3){
			criteria.andEmailEqualTo(data);
		}else{
			return TaotaoResult.build(400, "参数包含非法数据");
		}
		// 执行查询
		List<TbUser> list = userMapper.selectByExample(example );
		if (list!=null&&list.size()>0) {
			// 查询到数据，返回false
			return TaotaoResult.ok(false);
		}
		// 数据可用的情况
		return TaotaoResult.ok(true);
	}

	@Override
	public TaotaoResult register(TbUser user) {
		// 检查数据的有效性
		if (StringUtils.isBlank(user.getUsername())) {
			return TaotaoResult.build(400, "用户名不能为空");
		}
		// 1 判断用户名是否重复
		TaotaoResult r = checkDate(user.getUsername(), 1);
		if(!(boolean)r.getData()){
			return TaotaoResult.build(400, "用户名重复");
		}
		// 2 判断密码是否为空
		if (StringUtils.isBlank(user.getPassword())) {
			return TaotaoResult.build(400, "密码不能为空");
		}
		// 3
		if (StringUtils.isNotBlank(user.getPhone())) {
			// 是否重复校验
			r = checkDate(user.getPhone(), 2);
			if (!(boolean)r.getData()) {
				return TaotaoResult.build(400, "手机号重复");
			}
		}
		// 4 email校验
		if (StringUtils.isNotBlank(user.getEmail())) {
			// 是否重复校验
			r = checkDate(user.getEmail(), 3);
			if (!(boolean)r.getData()) {
				return TaotaoResult.build(400, "email重复");
			}
		}
		
		// 补全数据库数据pojo
		user.setCreated(new Date());
		user.setUpdated(new Date());
		// 密码要进行md5加密
		String md5Pass = DigestUtils.md5Hex(user.getPassword().getBytes());
		user.setPassword(md5Pass);
		// 插入数据
		userMapper.insert(user);
		// 返回注册成功
		return TaotaoResult.ok();
	}

}
