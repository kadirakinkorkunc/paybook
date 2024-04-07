package com.kkorkunc.paybook.booker;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kkorkunc.paybook.booker.OrderService.Order;
import com.kkorkunc.paybook.booker.OrderService.StartOrder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookerController {

    BookerService bookerService;

    @PostMapping
    public BuyResponse buy(@RequestHeader(name = "X-User-Id", required = true) String userId,
            @RequestBody @Valid BuyRequest bookingRequest) throws Exception {

        return BuyResponse.from(bookerService.buy(bookingRequest.toStartOrder(userId)));
    }

    public record BuyRequest(

            @NotNull Long bookId,

            @NotNull @Min(1) Long qty) {

        public StartOrder toStartOrder(String userId) {
            return new StartOrder(bookId, qty, userId);
        }
    }

    public record BuyResponse (
        String orderId
    ) {

        public static BuyResponse from(Order order) {
            return new BuyResponse(order.getId());
        }
    }
}
