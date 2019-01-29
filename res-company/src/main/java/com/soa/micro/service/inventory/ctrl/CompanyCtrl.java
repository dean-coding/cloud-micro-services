package com.soa.micro.service.inventory.ctrl;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.soa.micro.service.inventory.client.UserFeignClient;
import com.soa.micro.service.inventory.entity.Company;
import com.soa.micro.service.inventory.entity.User;
import com.soa.micro.service.inventory.repo.CompanyRepo;
import com.soa.micro.service.inventory.ribbon.RibbonHystrixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaRegistration;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.common.base.Predicate;

@RestController
@RequestMapping("/company")
public class CompanyCtrl {

	@Autowired
	private UserFeignClient userFeignClient;

	@GetMapping("/details")
	public Company findByIdFeign() {
		return new Company("总公司", this.userFeignClient.query());
	}

	@Autowired
	private CompanyRepo companyRepo;
	@Autowired
	private EurekaRegistration regInfo;

	public final String info() {
		if (regInfo != null) {
			return "port:" + regInfo.getNonSecurePort() + ",serviceId:" + regInfo.getServiceId();
		} else {
			return "未获取到实例信息";
		}
	}

	@GetMapping("/list")
	public List<Company> list() {
		return this.companyRepo.findAll();
	}

	public static final int MAX_RETRY_NUM = 3;

	private boolean isOver = false;

	@Transactional
	public Boolean staffLeaveByConn(Long id) {
		Assert.isTrue(!isOver, "所有员工都离开了");
		Company findOne = this.companyRepo.findOne(id);
		if (findOne == null)
			return false;
		if (findOne.getStaffCount() <= 0) {
			this.isOver = true;
			return false;
		}
		// Mysql行锁处理并发
		int updateStaffCount = this.companyRepo.subStaffCountByConn(id, 1);
		if (updateStaffCount <= 0) {// 更新失败，重试
			return true;
		} else {
			return false;
		}
	}

	public Boolean staffLeaveByVersion(Long id) {
		Assert.isTrue(!isOver, "所有员工都离开了");
		Company findOne = this.companyRepo.findOne(id);
		if (findOne == null)
			return false;
		if (findOne.getStaffCount() <= 0) {
			this.isOver = true;
			return false;
		}
		Integer staffCountBefore = findOne.getStaffCount();
		if (staffCountBefore == null || staffCountBefore <= 0) {
			return false;
		}
		int ver = findOne.getVersion();
		// 乐观锁处理并发
		int updateStaffCount = this.companyRepo.updateStaffCount(id, ver, --staffCountBefore);
		if (updateStaffCount <= 0) {// 更新失败，重试
			return true;
		} else {
			return false;
		}
	}

	@RequestMapping("/{id}/leave-by-version")
	public void retryHandler(@PathVariable("id") Long id) {
		RetryerBuilder<Boolean> newBuilder = RetryerBuilder.newBuilder();
		Retryer<Boolean> retryer = newBuilder.retryIfResult(new Predicate<Boolean>() {// 设置自定义段元重试源，
			@Override
			public boolean apply(Boolean state) {// 特别注意：这个apply返回true说明需要重试，与操作逻辑的语义要区分
				return state;
			}
		}).withStopStrategy(StopStrategies.stopAfterAttempt(3))// 设置重试5次，同样可以设置重试超时时间
				.withWaitStrategy(WaitStrategies.fixedWait(100L, TimeUnit.MILLISECONDS)).build();// 设置每次重试间隔

		try {
			// 重试入口采用call方法，用的是java.util.concurrent.Callable<V>的call方法,所以执行是线程安全的
			retryer.call(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					try {
						// 特别注意：返回false说明无需重试，返回true说明需要继续重试
						return staffLeaveByVersion(id);
					} catch (Exception e) {
						throw new Exception(e);
					}
				}
			});

		} catch (ExecutionException e) {
		} catch (RetryException ex) {
			ex.printStackTrace();
		}
	}

	@RequestMapping("/{id}/leave-by-save")
	public void staffLeaveBySave(@PathVariable("id") Long id) {
		Company findOne = this.companyRepo.findOne(id);
		if (findOne == null)
			return;
		Integer staffCountBefore = findOne.getStaffCount();
		if (staffCountBefore == null || staffCountBefore <= 0) {
			return;
		}
		findOne.setStaffCount(--staffCountBefore);
		try {
			this.companyRepo.save(findOne);
		} catch (ObjectOptimisticLockingFailureException e) {
			// System.err.println("更新失败");
			this.staffLeaveBySave(id);
		}
	}

	@Autowired
	private RibbonHystrixService ribbonHystrixService;

	@GetMapping("/users/{id}")
	public User findById(@PathVariable Long id) {
		return this.ribbonHystrixService.findById(id);
	}
}
