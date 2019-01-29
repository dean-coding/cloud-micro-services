package com.soa.micro.service.order.client;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 使用@FeignClient("res-user")注解绑定res-user服务，还可以使用url参数指定一个URL。 调用其他资源服务
 * 异常处理: UserFeignClientHystrixFactory implements FallbackFactory<OrderFeignClient>
 * 注意：@FeignClient fallback 默认情况下不会起作用，需要修改配置文件：
 * client:
     hystrix:
      enabled: true
 */
@FeignClient(name = "res-service-inventory", fallbackFactory = InventoryClient.InventoryFeignClientHystrixFactory.class)
public interface InventoryClient {

	/**
	 * 新增库存
	 *
	 * @param id
	 * @param count
	 * @return
	 */
	@RequestMapping(name = "/inc", method = RequestMethod.PUT)
	public boolean inc(@RequestParam("id") Long id, @RequestParam("count") int count);

	/**
	 * 减少库存
	 *
	 * @param id
	 * @param count
	 * @return
	 */
	@RequestMapping(name = "/sub", method = RequestMethod.PUT)
	public boolean sub(@RequestParam("id") Long id, @RequestParam("count") int count);

	/**
	 * 库存服务熔断器
	 */
	@Slf4j
	@Component
	static class InventoryFeignClientHystrixFactory implements FallbackFactory<InventoryClient> {

		@Override
		public InventoryClient create(Throwable cause) {
			return new InventoryClient() {
				@Override
				public boolean inc(Long id, int count) {
					log.warn("调度库存服务inc失败：id={},count={}", id, count);
					return false;
				}

				@Override
				public boolean sub(Long id, int count) {
					log.warn("调用库存服务sub失败：id={},count={}", id, count);
					return false;
				}
			};
		}
	}
}
