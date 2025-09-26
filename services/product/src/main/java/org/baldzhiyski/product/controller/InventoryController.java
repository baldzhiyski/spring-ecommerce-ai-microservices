package org.baldzhiyski.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.baldzhiyski.product.model.req.ReserveCommand;
import org.baldzhiyski.product.model.res.ReserveResponse;
import org.baldzhiyski.product.service.InventoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products") @RequiredArgsConstructor
public class InventoryController {
    private final InventoryService svc;

    @PostMapping("/inventory/reserve")
    public ReserveResponse reserve(@RequestBody @Valid ReserveCommand cmd) { return svc.reserve(cmd); }

    @PostMapping("/inventory/confirm")
    public void confirm(@RequestParam String orderRef) { svc.confirm(orderRef); }

    @PostMapping("/inventory/cancel")
    public void cancel(@RequestParam String orderRef) { svc.cancel(orderRef); }
}
