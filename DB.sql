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
    total_quantity int                                 not null,
    created_at     timestamp default CURRENT_TIMESTAMP null
);

create index idx_user_status
    on purchase_order (user_id, order_status);



-- 5. 주문 상품 정보
create table purchase_order_item
(
    order_item_no   bigint auto_increment        primary key,
    order_no        bigint                                not null,
    inventory_no    bigint                                not null,
    order_quantity  int                                   not null,
    cancel_quantity int         default 0                 null,
    item_status     varchar(20) default 'ORDERED'         null comment '(ORDERED, CANCELLED)',
    snapshot_info   varchar(255)                          null,
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


-- 3. 상품별 최초 이력 생성
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 1, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 2, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 3, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 4, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 5, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 6, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 7, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 8, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 9, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 10, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 11, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 12, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 13, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 14, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 15, null, null);
INSERT INTO store_db.product_inventory_history (inventory_no, last_order_no, last_order_time) VALUES ( 16, null, null);


select * from product;
select * from product_inventory;
select * from product_inventory_history;
