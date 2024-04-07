package com.kkorkunc.paybook.booker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.kkorkunc.paybook.booker.OrderService.Order;
import com.kkorkunc.paybook.booker.OrderService.StartOrder;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookerService {

    Logger logger = LogManager.getLogger(getClass());

    OrderService orderService;

    InventoryService inventoryService;

    PaymentService paymentService;

    public Order buy(StartOrder startOrder) throws Exception {
        var startedOrder = orderService.startOrder(startOrder);

        inventoryService.reserve(startedOrder.getBookId(), startedOrder.getQty());
        paymentService.pay(startedOrder.getId(), startedOrder.getTotalAmount());

        var completedOrder = orderService.completeOrder(startedOrder.getId());

        logger.debug("Order completed with id:{}, total amount:{} for user:{}", completedOrder.getId(), completedOrder.getTotalAmount().toString(), completedOrder.getUserId());
        return completedOrder;
    }
}
