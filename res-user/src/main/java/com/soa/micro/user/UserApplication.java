package com.soa.micro.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;


@SpringBootApplication
@EnableDiscoveryClient // 启用Eureka发现功能
@EnableHystrixDashboard
public class UserApplication {

	public static void main(String[] args) {

		SpringApplication.run(UserApplication.class, args);
	}
}
