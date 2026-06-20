package com.origin.service;

import com.origin.dto.OrderProcessDTO;
import com.origin.entity.CustomerOrder;
import java.util.List;

/**
 * 订单服务接口
 * 提供客户订单的创建、处理（责任链模式）和状态管理
 */
public interface OrderService {
    /**
     * 查询所有客户订单
     * */
    List<CustomerOrder> findAll();
    /**
     * 根据ID查询客户订单
     * */
    CustomerOrder findById(Long id);
    /**
     *  创建客户订单
     *  */
    CustomerOrder create(OrderProcessDTO dto);
    /**
     *  处理订单（责任链：库存锁定→生成拣货单→打印物流单→更新库存）
     *  */
    CustomerOrder processOrder(Long id);
    /**
     * 更新订单状态
     * */
    CustomerOrder updateStatus(Long id, String status);
    /**
     *  取消客户订单
     *  */
    void cancel(Long id);
}
