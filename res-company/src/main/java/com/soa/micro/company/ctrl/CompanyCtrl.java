package com.soa.micro.company.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.soa.micro.company.entity.Company;
import com.soa.micro.company.entity.User;
import com.soa.micro.company.feign.UserFeignClient;
import com.soa.micro.company.ribbon.RibbonHystrixService;

@RestController
public class CompanyCtrl {

	@Autowired
	private UserFeignClient userFeignClient;

	@GetMapping("/details")
	public Company findByIdFeign() {
		return new Company("总公司", this.userFeignClient.query());
	}

	@Autowired
	private RibbonHystrixService ribbonHystrixService;

	@GetMapping("/users/{id}")
	public User findById(@PathVariable Long id) {
		return this.ribbonHystrixService.findById(id);
	}
}
