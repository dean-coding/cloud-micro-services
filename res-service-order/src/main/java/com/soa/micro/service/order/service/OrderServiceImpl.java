package com.soa.micro.service.order.service;


import com.soa.micro.service.order.client.InventoryClient;
import com.soa.micro.service.order.entity.OrderEntity;
import com.soa.micro.service.order.repo.OrderRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

/**
 * @author fuhw/DeanKano
 * @date 2019-01-28 21:38
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private InventoryClient inventoryClient;

    /**
     * LcnTransaction 分布式事务管理
     *
     * Transactional 本地事务管理
     * @param entity
     * @return
     */
//    @LcnTransaction
    @Transactional
    @Override
    public OrderEntity save(OrderEntity entity) {
        log.info("当前方法是否处于事务中：{}", TransactionSynchronizationManager.isActualTransactionActive());
        if (entity == null) {
            return null;
        }
        // local transaction
        entity = orderRepo.save(entity);

        // remote call inventory service (distributed transaction)
        inventoryClient.inc(entity.getId(),entity.getItemCount());

        return entity;
    }






    @Transactional
    @Override
    public boolean updateByStatus(Long id, String status) {
        log.info("当前方法是否处于事务中：{}", TransactionSynchronizationManager.isActualTransactionActive());
        boolean isExists = orderRepo.exists(id);
        if (StringUtils.isEmpty(status) || id == null || !isExists) {
            log.warn("参数异常: id={},status={},记录是否存在:{}", id, status, isExists);
            return false;
        }
        return orderRepo.updateByStatus(id, status) > 0;
    }

}
