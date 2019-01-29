package com.soa.micro.service.order.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fuhw/DeanKano
 * @date 2019-01-29 10:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void updateByStatus() {
        boolean success = orderService.updateByStatus(1L, "success");
        Assert.assertFalse(success);
    }

}