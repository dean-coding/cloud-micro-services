package com.soa.micro.user.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.soa.micro.user.client.UserFeignClient.UserFeignClientHystrixFactory;

import feign.hystrix.FallbackFactory;

/**
 * 使用@FeignClient("res-user")注解绑定res-user服务，还可以使用url参数指定一个URL。 
 * 调用其他资源服务 异常处理:
 * UserFeignClientHystrixFactory implements FallbackFactory<UserFeignClient>
 * 注意：
 * @FeignClient fallback 默认情况下不会起作用，需要修改配置文件： client: hystrix: enabled: true
 */
@FeignClient(name = "res-user-c", fallbackFactory = UserFeignClientHystrixFactory.class)
public interface UserFeignClient {

	//注意: 用@PathVariable(“id”)的形式获取不到参数
	@RequestMapping("/users/exists")
	public boolean exist(@RequestParam("id") Long id);

	@Component
	static class UserFeignClientHystrixFactory implements FallbackFactory<UserFeignClient> {
		@Override
		public UserFeignClient create(Throwable arg0) {
			return new UserFeignClient() {
				@Override
				public boolean exist(Long id) {
					System.err.println("异常");
					return false;
				}
			};
		}

	}

}
