package com.kkorkunc.paybook.booker;

import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.kkorkunc.paybook.booker.OrderService.Order.Status;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    
    Logger logger = LogManager.getLogger(getClass());

    CatalogService catalogService;

    Map<String, Order> orderList = new ConcurrentHashMap<String, Order>();

    public Order startOrder(StartOrder startOrder) throws Exception {
        var orderId = UUID.randomUUID();
        var userId = startOrder.userId();

        //dummy business logic
        if(doesOrderExists(userId)) {
            logger.error("user with id:{} tried to buy more than once");
            throw new Exception("order.limitExceeded");
        }

        String orderIdStr = orderId.toString();

        var bookId = startOrder.bookId();
        var requestedQty = startOrder.qty();
        var totalAmount = catalogService.get(bookId).amount().multiply(BigDecimal.valueOf(requestedQty));
        var startedOrder = new Order(orderIdStr, bookId, requestedQty, userId, totalAmount, Status.PROCESSING);
        orderList.put(orderIdStr, startedOrder);
        logger.debug("Order({}) initiated for user({})", orderIdStr, userId);
        return startedOrder;
    }

    private boolean doesOrderExists(String userId) {
        return orderList.values().stream().anyMatch(order -> order.userId.contentEquals(userId));
    }

    public static record StartOrder (Long bookId, Long qty, String userId) {}

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Order {
        String id; 
        Long bookId; 
        Long qty; 
        String userId; 
        BigDecimal totalAmount;
        Status status;

        public enum Status {
            PROCESSING,
            COMPLETED,
            FAILED
        }
    }

    public Order completeOrder(String id) throws Exception {
        var order = orderList.get(id);
        if(order == null) {
            logger.error("order not found with id:{}", id);
            throw new Exception("order.notFound");
        }

        order.setStatus(Status.COMPLETED);
        return order;
    }

}
