package com.soa.micro.service.order.ctrl;

import com.soa.micro.service.order.entity.OrderEntity;
import com.soa.micro.service.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单ctrl
 *
 * @author fuhw/DeanKano
 * @date 2019-01-29 11:44
 */
@RestController
@RequestMapping("order")
public class OrderCtrl {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public boolean createOrder(OrderEntity orderEntity) {

        return orderService.save(orderEntity) != null;
    }
}
