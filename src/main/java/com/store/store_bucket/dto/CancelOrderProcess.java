package com.store.store_bucket.dto;

import com.store.store_bucket.entity.PurchaseOrder;
import com.store.store_bucket.entity.PurchaseOrderItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CancelOrderProcess {
    private PurchaseOrder purchaseOrder;
    private List<PurchaseOrderItem> purchaseOrderItems;

    private boolean isCancelSuccess;

    public void fail() {
        this.isCancelSuccess = false;
    }

    public void success() {
        this.isCancelSuccess = true;
    }
}
