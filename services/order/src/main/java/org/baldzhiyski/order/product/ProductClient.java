package org.baldzhiyski.order.product;

import org.baldzhiyski.order.model.OrderLine;
import org.baldzhiyski.order.model.req.PurchaseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="product-client", url="${application.config.product-url}")
public interface ProductClient {
    @PostMapping("/inventory/reserve")
    ReserveResponse reserve(@RequestBody ReserveCommand cmd);

    @PostMapping("/inventory/confirm")
    void confirm(@RequestParam("orderRef") String orderRef);

    @PostMapping("/inventory/cancel")
    void cancel(@RequestParam("orderRef") String orderRef);
}
