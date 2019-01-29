package com.soa.micro.service.inventory.client;

import java.util.Collections;
import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import com.soa.micro.service.inventory.entity.User;
import com.soa.micro.service.inventory.client.UserFeignClient.UserFeignClientHystrixFactory;

import feign.hystrix.FallbackFactory;

/**
 * 使用@FeignClient("res-user")注解绑定res-user服务，还可以使用url参数指定一个URL。 调用其他资源服务
 * 异常处理: UserFeignClientHystrixFactory implements FallbackFactory<UserFeignClient> 
 * 注意：@FeignClient fallback 默认情况下不会起作用，需要修改配置文件：
 * client:
     hystrix:
      enabled: true
 */
@FeignClient(name = "res-user", fallbackFactory = UserFeignClientHystrixFactory.class)
public interface UserFeignClient {

	@RequestMapping("/users")
	public List<User> query();

	// static class UserFeignClientHystrix implements UserFeignClient {
	//
	// @Override
	// public List<User> query() {
	// System.err.println("异常");
	// return Collections.emptyList();
	// }
	//
	// }

	@Component
	static class UserFeignClientHystrixFactory implements FallbackFactory<UserFeignClient> {
		@Override
		public UserFeignClient create(Throwable arg0) {
			return new UserFeignClient() {
				@Override
				public List<User> query() {
					System.err.println("异常");
					return Collections.emptyList();
				}
			};
		}

	}

}
