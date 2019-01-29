package com.soa.micro.service.order.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 订单实体
 *
 * @author fuhw/DeanKano
 * @date 2019-01-28 20:03
 */
@Data
@Entity
@Table(name = "service_order")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id ;

    private String orderNo;

    private String status;

    private Integer itemCount;
}
