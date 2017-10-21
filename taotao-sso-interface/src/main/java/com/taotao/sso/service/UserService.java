package com.taotao.sso.service;

import com.taotao.common.utils.TaotaoResult;
import com.taotao.pojo.TbUser;

public interface UserService {
	// 检查用户数据
	TaotaoResult checkDate(String param,Integer type);
	// 用户注册 post
	TaotaoResult register(TbUser tbUser);
}
