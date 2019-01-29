package com.soa.micro.service.order.ctrl;

import org.springframework.web.bind.annotation.RestController;

import com.soa.micro.user.client.UserFeignClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 实现依赖包(res-user)的形式:
 * 调用 UserFeignClient 接口,自定义实现
 *
 * @author fuhw/DeanKano
 * @date 2019-01-14
 */
@Slf4j
@RestController
public class UserCtrlByDependentUserFeignClient implements UserFeignClient {

	@Override
	public boolean exist(Long id) {
		log.info("用依赖的方式调用feignclient服务,参数:id={}", id);
		return true;
	}

}
