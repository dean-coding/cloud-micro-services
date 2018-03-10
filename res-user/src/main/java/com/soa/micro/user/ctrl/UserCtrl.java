package com.soa.micro.user.ctrl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.soa.micro.user.entity.User;
import com.soa.micro.user.repo.UserRepo;

@RestController
@RefreshScope
public class UserCtrl implements InitializingBean {
	@Autowired
	private UserRepo userRepo;

	@Value("${curprofile}")
	private String curprofile;

	@Autowired
	private EurekaRegistration regInfo;

	@GetMapping("/instance-info")
	public String info() {
		if (regInfo != null) {
			return "port:" + regInfo.getNonSecurePort() + ",serviceId:" + regInfo.getServiceId();
		} else {
			return "未获取到实例信息";
		}
	}

	@GetMapping("/users")
	public List<User> query() {
		System.err.println("当前实例：" + this.info());
		return this.userRepo.findAll();
	}

	@GetMapping("/users/{id}")
	public User findByIdFeign(@PathVariable("id") Long id) {
		return this.userRepo.findOne(id);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.err.println("当前的配置：" + curprofile);
	}

}