package com.soa.micro.service.inventory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.soa.micro.service.inventory.ctrl.CompanyCtrl;
import com.soa.micro.service.inventory.entity.Company;
import com.soa.micro.service.inventory.repo.CompanyRepo;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CompanyApplicationTests {

	@Autowired
	private CompanyCtrl ctrl;

	@Autowired
	private CompanyRepo repo;

	private static ExecutorService executors = Executors.newFixedThreadPool(10);

	@Before
	public void initValue() {
		Company company = new Company();
		company.setName("demo");
		company.setStaffCount(100);
		company.setVersion(0);
		this.repo.save(company);
	}

	@After
	public void afterHandler() {
		this.repo.deleteAll();
	}

	@Test
	public void list() {
		List<Company> list = ctrl.list();
		Assert.assertNotNull(list);
		Assert.assertEquals(list.size(), 1);
	}

	@Test
	public void singleThreadUpdate() {
		List<Company> list = ctrl.list();
		Company company = list.get(0);
		Long id = company.getId();
		for (int i = 0; i < 100; i++) {
			ctrl.staffLeaveByVersion(id);
		}
		Company findOne = repo.findOne(id);
		Assert.assertNotNull(findOne);
		Assert.assertEquals(findOne.getStaffCount().intValue(), 0);

	}

	@Test
	public void concurrentUpdateByVersion() throws InterruptedException {
		List<Company> list = ctrl.list();
		Company company = list.get(0);
		Long id = company.getId();
		for (int i = 0; i < 100; i++) {
			executors.execute(() -> {
				ctrl.retryHandler(id);
				System.err.println(Thread.currentThread().getName() + "executing.....");
			});
		}
		executors.shutdown();
		while (true) {
			if (executors.isTerminated()) {
				System.err.println("所有任务执行完成");
				Company findOne = repo.findOne(id);
				Assert.assertNotNull(findOne);
				Assert.assertTrue(findOne.getStaffCount().intValue() >= 0);
				break;
			}
		}
	}

	@Test
	public void concurrentUpdateByConn() throws InterruptedException {
		List<Company> list = ctrl.list();
		Company company = list.get(0);
		Long id = company.getId();
		for (int i = 0; i < 1000; i++) {
			executors.execute(() -> {
				ctrl.staffLeaveByConn(id);
				System.err.println(Thread.currentThread().getName() + "executing.....");
			});
		}
		executors.shutdown();
		while (true) {
			if (executors.isTerminated()) {
				System.err.println("所有任务执行完成");
				Company findOne = repo.findOne(id);
				Assert.assertNotNull(findOne);
				Assert.assertEquals(findOne.getStaffCount().intValue(), 0);
				break;
			}
		}
	}

}
