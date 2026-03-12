package com.store.store_bucket.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.store.store_bucket.dto.ProductInventoryHistoryLastOrder;
import com.store.store_bucket.entity.QProductInventory;
import com.store.store_bucket.entity.QProductInventoryHistory;
import com.store.store_bucket.enums.ActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class ProductInventoryHistoryRepositoryCustomImpl implements ProductInventoryHistoryRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductInventoryHistoryLastOrder> findProductInventoryHistoryLastOrders(String actionType) {
        QProductInventory pi = QProductInventory.productInventory;
        QProductInventoryHistory pih = QProductInventoryHistory.productInventoryHistory;
        QProductInventoryHistory subPih = new QProductInventoryHistory("subPih");

        return queryFactory
                .select(Projections.constructor(ProductInventoryHistoryLastOrder.class,
                        pi.product.productId,
                        pi.color,
                        pi.size,
                        pi.quantity,
                        pih.lastOrderNo,
                        pih.lastOrderTime
                ))
                .from(pi)
                .leftJoin(pih).on(pi.inventoryNo.eq(pih.inventoryNo))
                .where(
                        isLatestLog(pi, pih, subPih),
                        eqActionType(pih, actionType)
                )
                .fetch();
    }

    private BooleanExpression isLatestLog(QProductInventory i, QProductInventoryHistory h, QProductInventoryHistory subH) {
        return h.logNo.eq(
                JPAExpressions
                        .select(subH.logNo.max())
                        .from(subH)
                        .where(subH.inventoryNo.eq(i.inventoryNo))
        ).or(h.logNo.isNull());
    }

    private BooleanExpression eqActionType(QProductInventoryHistory h, String actionType) {
        if (!StringUtils.hasText(actionType)){
            return null;
        }
        return h.actionType.eq(ActionType.valueOf(actionType.toUpperCase()));
    }
}
