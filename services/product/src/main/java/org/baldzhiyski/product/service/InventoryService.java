package org.baldzhiyski.product.service;

import org.baldzhiyski.product.model.req.ReserveCommand;
import org.baldzhiyski.product.model.res.ReserveResponse;

public interface InventoryService {
    ReserveResponse reserve(ReserveCommand cmd);

    void confirm(String orderRef);

    void cancel(String orderRef);
}
