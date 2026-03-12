-- 1. 상품 마스터
create table product
(
    product_no int auto_increment        primary key,
    product_id varchar(50) not null,
    constraint product_id
        unique (product_id)
);

-- 2. 상품 재고
create table product_inventory
(
    inventory_no bigint auto_increment        primary key,
    product_no   int           not null,
    product_id   varchar(50)   not null,
    color        varchar(20)   not null,
    size         varchar(20)   not null,
    quantity     int default 0 not null,
    constraint uq_product_option
        unique (product_no, color, size),
    constraint product_inventory_ibfk_1
        foreign key (product_no) references product (product_no)
);

-- 3. 상품 재고 이력
create table product_inventory_history
(
    log_no          bigint auto_increment        primary key,
    action_type      varchar(20)                 not null,
    inventory_no    bigint   null,
    last_order_no   bigint   null,
    last_order_time datetime null
);


-- 4. 주문 마스터
create table purchase_order
(
    order_no       bigint auto_increment        primary key,
    user_id        varchar(50)                         not null,
    order_status   varchar(20)                         not null,
    created_at     timestamp default CURRENT_TIMESTAMP null
);

create index idx_user_status
    on purchase_order (user_id, order_status);



-- 5. 주문 상품 정보
create table purchase_order_item
(
    order_item_no   bigint auto_increment        primary key,
    order_no        bigint                                not null,
    inventory_no    bigint                                null,
    order_quantity  int                                   not null,
    cancel_quantity int         default 0                 null,
    item_status     varchar(20) default 'ORDERED'         null comment '(ORDERED, CANCELLED)',
    created_at      timestamp   default CURRENT_TIMESTAMP null,
    cancel_at       timestamp                             null,
    constraint purchase_order_item_ibfk_1
        foreign key (order_no) references purchase_order (order_no),
    constraint purchase_order_item_ibfk_2
        foreign key (inventory_no) references product_inventory (inventory_no)
);

create index inventory_no
    on purchase_order_item (inventory_no);

create index order_no
    on purchase_order_item (order_no);



-- 5. 주문 상태/수량 변경 이력 테이블
create table purchase_order_item_history
(
    history_no       bigint auto_increment        primary key,
    order_no         bigint                              not null,
    order_item_no    bigint                              null,
    action_type      varchar(20)                         not null,
    changed_quantity int                                 not null,
    reason           varchar(255)                        null,
    created_at       timestamp default CURRENT_TIMESTAMP null
);



-- 6. API 로그 테이블
create table api_log
(
    log_no      bigint auto_increment        primary key,
    api_path    varchar(100)                        null,
    method      varchar(10)                         null,
    user_id     varchar(50)                         null,
    status_code int                                 null,
    message     text                                null,
    created_at  timestamp default CURRENT_TIMESTAMP null
);


-- 1. 상품 마스터 등록
INSERT INTO store_db.product ( product_id) VALUES ('11101JS505');
INSERT INTO store_db.product ( product_id) VALUES ('82193SRK52');
INSERT INTO store_db.product ( product_id) VALUES ('M31E5AC014');

-- 2. 상품별 재고(100개) 등록
-- product_id 추가
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 1, '11101JS505', 'BK', '95', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 1, '11101JS505', 'WH', '95', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 1, '11101JS505', 'BK', '100', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 1, '11101JS505', 'WH', '100', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 1, '11101JS505', 'BK', '105', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 1, '11101JS505', 'WH', '105', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 2, '82193SRK52', 'GY', '1', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 2, '82193SRK52', 'BK', '1', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 2, '82193SRK52', 'GY', '2', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 2, '82193SRK52', 'BK', '2', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 2, '82193SRK52', 'GY', '3', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 2, '82193SRK52', 'BK', '3', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 2, '82193SRK52', 'GY', '4', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 2, '82193SRK52', 'BK', '4', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 3, 'M31E5AC014', 'OR', 'FREE', 100);
INSERT INTO store_db.product_inventory (product_no, product_id, color, size, quantity) VALUES ( 3, 'M31E5AC014', 'IV', 'FREE', 100);

-- 3. 상품별 최종 주문 이력
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (1, 'ORDER', 3, 1, '2026-03-12 13:12:49');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (2, 'ORDER', 6, 1, '2026-03-12 13:12:49');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (3, 'ORDER', 9, 2, '2026-03-12 13:12:49');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (4, 'ORDER', 15, 2, '2026-03-12 13:12:49');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (5, 'ORDER', 2, 5, '2026-03-12 13:12:50');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (6, 'ORDER', 16, 5, '2026-03-12 13:12:50');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (7, 'CANCEL', 2, 5, '2026-03-12 13:12:50');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (8, 'ORDER', 2, 6, '2026-03-12 13:12:50');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (9, 'ORDER', 16, 6, '2026-03-12 13:12:50');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (10, 'ORDER', 3, 7, '2026-03-12 13:31:10');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (11, 'ORDER', 6, 7, '2026-03-12 13:31:10');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (12, 'ORDER', 9, 8, '2026-03-12 13:31:10');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (13, 'ORDER', 15, 8, '2026-03-12 13:31:10');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (14, 'ORDER', 2, 11, '2026-03-12 13:31:10');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (15, 'ORDER', 16, 11, '2026-03-12 13:31:10');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (16, 'CANCEL', 2, 11, '2026-03-12 13:31:11');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (17, 'ORDER', 2, 12, '2026-03-12 13:31:11');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (18, 'ORDER', 16, 12, '2026-03-12 13:31:11');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (19, 'ORDER', 3, 13, '2026-03-12 13:38:01');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (20, 'ORDER', 6, 13, '2026-03-12 13:38:01');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (21, 'ORDER', 9, 14, '2026-03-12 13:38:01');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (22, 'ORDER', 15, 14, '2026-03-12 13:38:01');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (23, 'ORDER', 2, 17, '2026-03-12 13:38:01');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (24, 'ORDER', 16, 17, '2026-03-12 13:38:01');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (25, 'CANCEL', 2, 17, '2026-03-12 13:38:01');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (26, 'ORDER', 2, 18, '2026-03-12 13:38:02');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (27, 'ORDER', 16, 18, '2026-03-12 13:38:02');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (28, 'ORDER', 3, 19, '2026-03-12 13:38:18');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (29, 'ORDER', 6, 19, '2026-03-12 13:38:18');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (30, 'ORDER', 9, 20, '2026-03-12 13:38:18');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (31, 'ORDER', 15, 20, '2026-03-12 13:38:18');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (32, 'ORDER', 2, 23, '2026-03-12 13:38:18');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (33, 'ORDER', 16, 23, '2026-03-12 13:38:18');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (34, 'CANCEL', 2, 23, '2026-03-12 13:38:18');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (35, 'ORDER', 2, 24, '2026-03-12 13:38:18');
INSERT INTO store_db.product_inventory_history (log_no, action_type, inventory_no, last_order_no, last_order_time) VALUES (36, 'ORDER', 16, 24, '2026-03-12 13:38:18');

-- 4. 주문
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (1, 'user_test1', 'COMPLETED', '2026-03-12 13:12:49');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (2, 'hyun_test1', 'COMPLETED', '2026-03-12 13:12:49');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (3, 'test1', 'FAILED', '2026-03-12 13:12:50');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (4, 'test1', 'FAILED', '2026-03-12 13:12:50');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (5, 'cancel_test', 'COMPLETED', '2026-03-12 13:12:50');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (6, 'cancel_test', 'COMPLETED', '2026-03-12 13:12:50');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (7, 'user_test1', 'COMPLETED', '2026-03-12 13:31:10');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (8, 'hyun_test1', 'COMPLETED', '2026-03-12 13:31:10');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (9, 'test1', 'FAILED', '2026-03-12 13:31:10');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (10, 'test1', 'FAILED', '2026-03-12 13:31:10');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (11, 'cancel_test', 'COMPLETED', '2026-03-12 13:31:10');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (12, 'cancel_test', 'COMPLETED', '2026-03-12 13:31:11');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (13, 'user_test1', 'COMPLETED', '2026-03-12 13:38:01');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (14, 'hyun_test1', 'COMPLETED', '2026-03-12 13:38:01');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (15, 'test1', 'FAILED', '2026-03-12 13:38:01');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (16, 'test1', 'FAILED', '2026-03-12 13:38:01');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (17, 'cancel_test', 'COMPLETED', '2026-03-12 13:38:01');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (18, 'cancel_test', 'COMPLETED', '2026-03-12 13:38:02');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (19, 'user_test1', 'COMPLETED', '2026-03-12 13:38:17');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (20, 'hyun_test1', 'COMPLETED', '2026-03-12 13:38:18');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (21, 'test1', 'FAILED', '2026-03-12 13:38:18');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (22, 'test1', 'FAILED', '2026-03-12 13:38:18');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (23, 'cancel_test', 'COMPLETED', '2026-03-12 13:38:18');
INSERT INTO store_db.purchase_order (order_no, user_id, order_status, created_at) VALUES (24, 'cancel_test', 'COMPLETED', '2026-03-12 13:38:18');

-- 5 주문 상품 정보
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (1, 1, 3, 3, 0, 'COMPLETED', '2026-03-12 13:12:49', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (2, 1, 6, 1, 0, 'COMPLETED', '2026-03-12 13:12:49', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (3, 2, 9, 10, 0, 'COMPLETED', '2026-03-12 13:12:49', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (4, 2, 15, 5, 0, 'COMPLETED', '2026-03-12 13:12:49', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (5, 4, 3, 999, 0, 'FAILED', '2026-03-12 13:12:50', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (6, 4, 6, 999, 0, 'FAILED', '2026-03-12 13:12:50', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (7, 5, 2, 3, 1, 'COMPLETED', '2026-03-12 13:12:50', '2026-03-12 13:12:50');
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (8, 5, 16, 1, 0, 'COMPLETED', '2026-03-12 13:12:50', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (9, 6, 2, 3, 0, 'COMPLETED', '2026-03-12 13:12:50', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (10, 6, 16, 1, 0, 'COMPLETED', '2026-03-12 13:12:50', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (11, 7, 3, 3, 0, 'COMPLETED', '2026-03-12 13:31:10', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (12, 7, 6, 1, 0, 'COMPLETED', '2026-03-12 13:31:10', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (13, 8, 9, 10, 0, 'COMPLETED', '2026-03-12 13:31:10', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (14, 8, 15, 5, 0, 'COMPLETED', '2026-03-12 13:31:10', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (15, 10, 3, 999, 0, 'FAILED', '2026-03-12 13:31:10', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (16, 10, 6, 999, 0, 'FAILED', '2026-03-12 13:31:10', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (17, 11, 2, 3, 1, 'COMPLETED', '2026-03-12 13:31:10', '2026-03-12 13:31:11');
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (18, 11, 16, 1, 0, 'COMPLETED', '2026-03-12 13:31:10', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (19, 12, 2, 3, 0, 'COMPLETED', '2026-03-12 13:31:11', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (20, 12, 16, 1, 0, 'COMPLETED', '2026-03-12 13:31:11', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (21, 13, 3, 3, 0, 'COMPLETED', '2026-03-12 13:38:01', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (22, 13, 6, 1, 0, 'COMPLETED', '2026-03-12 13:38:01', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (23, 14, 9, 10, 0, 'COMPLETED', '2026-03-12 13:38:01', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (24, 14, 15, 5, 0, 'COMPLETED', '2026-03-12 13:38:01', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (25, 16, 3, 999, 0, 'FAILED', '2026-03-12 13:38:01', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (26, 16, 6, 999, 0, 'FAILED', '2026-03-12 13:38:01', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (27, 17, 2, 3, 1, 'COMPLETED', '2026-03-12 13:38:01', '2026-03-12 13:38:01');
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (28, 17, 16, 1, 0, 'COMPLETED', '2026-03-12 13:38:01', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (29, 18, 2, 3, 0, 'COMPLETED', '2026-03-12 13:38:02', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (30, 18, 16, 1, 0, 'COMPLETED', '2026-03-12 13:38:02', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (31, 19, 3, 3, 0, 'COMPLETED', '2026-03-12 13:38:17', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (32, 19, 6, 1, 0, 'COMPLETED', '2026-03-12 13:38:17', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (33, 20, 9, 10, 0, 'COMPLETED', '2026-03-12 13:38:18', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (34, 20, 15, 5, 0, 'COMPLETED', '2026-03-12 13:38:18', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (35, 22, 3, 999, 0, 'FAILED', '2026-03-12 13:38:18', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (36, 22, 6, 999, 0, 'FAILED', '2026-03-12 13:38:18', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (37, 23, 2, 3, 1, 'COMPLETED', '2026-03-12 13:38:18', '2026-03-12 13:38:18');
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (38, 23, 16, 1, 0, 'COMPLETED', '2026-03-12 13:38:18', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (39, 24, 2, 3, 0, 'COMPLETED', '2026-03-12 13:38:18', null);
INSERT INTO store_db.purchase_order_item (order_item_no, order_no, inventory_no, order_quantity, cancel_quantity, item_status, created_at, cancel_at) VALUES (40, 24, 16, 1, 0, 'COMPLETED', '2026-03-12 13:38:18', null);

-- 6 주문 취소 이력
INSERT INTO store_db.purchase_order_item_history (history_no, order_no, order_item_no, action_type, changed_quantity, reason, created_at) VALUES (1, 5, 7, 'CANCEL', 1, null, '2026-03-12 13:12:50');
INSERT INTO store_db.purchase_order_item_history (history_no, order_no, order_item_no, action_type, changed_quantity, reason, created_at) VALUES (2, 11, 17, 'CANCEL', 1, null, '2026-03-12 13:31:11');
INSERT INTO store_db.purchase_order_item_history (history_no, order_no, order_item_no, action_type, changed_quantity, reason, created_at) VALUES (3, 17, 27, 'CANCEL', 1, null, '2026-03-12 13:38:01');
INSERT INTO store_db.purchase_order_item_history (history_no, order_no, order_item_no, action_type, changed_quantity, reason, created_at) VALUES (4, 23, 37, 'CANCEL', 1, null, '2026-03-12 13:38:18');

# 조회
select * from product;
select * from product_inventory;
select * from product_inventory_history;

select * from purchase_order;
select * from purchase_order_item;
select * from purchase_order_item_history;

# 재고모니터링
SELECT i.product_id,
       i.color,
       i.size,
       i.quantity AS current_quantity,
       h.last_order_no,
       h.last_order_time
FROM product_inventory i
         LEFT JOIN product_inventory_history h ON i.inventory_no = h.inventory_no
WHERE h.log_no = (SELECT MAX(log_no)
                  FROM product_inventory_history
                  WHERE inventory_no = i.inventory_no)
   OR h.log_no IS NULL

;