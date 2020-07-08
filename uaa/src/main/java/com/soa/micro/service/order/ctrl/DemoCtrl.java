package com.soa.micro.service.order.ctrl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class DemoCtrl implements InitializingBean {

	@Value("${curprofile}")
	private String curprofile;

	@Autowired
	private EurekaRegistration regInfo;

	@GetMapping("/instance-info")
	public String info() {
		if (regInfo != null) {
			return "port:" + regInfo.getNonSecurePort() + ",serviceId:" + regInfo.getServiceId()+"\n";
		} else {
			return "未获取到实例信息\n";
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.err.println("当前的配置：" + curprofile);
	}

}