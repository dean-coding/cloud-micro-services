package com.soa.micro.service.order.repo;

import com.soa.micro.service.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author fuhw/DeanKano
 * @date 2019-01-28 20:05
 */
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findByOrderNo(String orderNo);

    @Modifying
    @Query("update #{#entityName} set status=?2 where id = ?1")
    int updateByStatus(Long id, String status);
}
