package com.soa.micro.service.order.service;


import com.soa.micro.service.order.entity.OrderEntity;

/**
 * 订单业务
 *
 * @author fuhw/DeanKano
 * @date 2019-01-28 21:37
 */
public interface OrderService {

    public OrderEntity save(OrderEntity entity);

    boolean updateByStatus(Long id, String status);

}
