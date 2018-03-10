# 用@FeignClient调用其他资源服务

1.依赖
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>

<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-feign</artifactId>
</dependency>

2.**.yml
server:
  port: 8011
spring:
  application:
    name: res-company
eureka:
  client:
    serviceUrl:
      defaultZone: http://discovery:8761/eureka/
  instance:
    preferIpAddress: true
feign:
  hystrix:
    enabled: true #默认未false，@FeignClient的fallback，一定要注意哦
    
3.使用：
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableCircuitBreaker
public class CompanyApplication {...}

4.容错处理：调用fallback
/**
 * 使用@FeignClient("res-user")注解绑定res-user服务，还可以使用url参数指定一个URL。 
 * 调用其他资源服务
 */
@FeignClient(name = "res-user",fallback=UserFeignClientHystrix.class)
public interface UserFeignClient {...}

@Component
UserFeignClientHystrix implements UserFeignClient {...}

5.容错处理：调用fallback(获取异常信息)

/**
 * 使用@FeignClient("res-user")注解绑定res-user服务，还可以使用url参数指定一个URL。 
 * 调用其他资源服务
 */
@FeignClient(name = "res-user", fallbackFactory = UserFeignClientHystrixFactory.class)
public interface UserFeignClient {...}

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

#Feign如果想要使用Hystrix Stream

我们知道Feign本身就是支持Hystrix的，可以直接使用
@FeignClient(value = "res-user", fallback = XXX.class) 
来指定fallback的类，这个fallback类集成@FeignClient所标注的接口即可。

但是假设我们需要使用Hystrix Stream进行监控，默认情况下，访问http://IP:PORT/hystrix.stream 是个404。如何为Feign增加Hystrix Stream支持呢？

需要以下两步：

第一步：添加依赖，示例：
<!-- 整合hystrix，其实feign中自带了hystrix，引入该依赖主要是为了使用其中的hystrix-metrics-event-stream，用于dashboard -->
<dependency>
 <groupId>org.springframework.cloud</groupId>
 <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
第二步：在启动类上添加@EnableCircuitBreaker 注解，示例：

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableCircuitBreaker
public class CompanyApplication {
 public static void main(String[] args) {
 SpringApplication.run(CompanyApplication.class, args);
 }
}
这样修改以后，访问任意的API后，再访问http://IP:PORT/hystrix.stream，就会展示出一大堆的API监控数据了。

    

# 用@LoadBalanced的restTemplate调用资源服务


/**
 * 实例化RestTemplate，通过@LoadBalanced注解开启均衡负载能力.
 * 
 * @return restTemplate
 */
@Bean
@LoadBalanced
public RestTemplate restTemplate() {
	return new RestTemplateBuilder().build();
}


/**
 * 使用@HystrixCommand注解指定当该方法发生异常时调用的方法
 * 
 * @param id
 *            id
 * @return 通过id查询到的用户
 */
@HystrixCommand(fallbackMethod = "fallback")
public User findById(Long id) {
	return this.restTemplate.getForObject("http://res-user/users/" + id, User.class);
}


/**
 * hystrix fallback方法
 * 
 * @param id
 *            id
 * @return 默认的用户
 */
public User fallback(Long id) {
	log.info("异常发生，进入fallback方法，接收的参数：id = {}", id);
	User user = new User();
	user.setId(-1L);
	user.setUsername("default username");
	user.setAge(0);
	return user;
}
