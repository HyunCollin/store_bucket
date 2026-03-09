# store
과제
# 개발 환경
- Spring boot 3.4.1
- JDK 17
- Mysql 8.0
- Docker desktop

docker command
- docker ps
- docker-compose logs -f app
- docker-compose logs -f db
- docker-compose start app
- docker-compose stop app
- docker-compose down
- docker-compose up -d --build
- docker-compose up -d --build app
- docker-compose stop app && docker-compose rm -f app

DB table 및 샘플 데이터 생성
- DB.sql 으로 table 생성 및 샘플 상품 데이터 생성
- 주요 API 요구사항을 충족하기 위한 조건을 table 반영

DB 수정 의사 결정 ( 1차 )
상품 재고 table 에 product_id 추가 
사유 : product_id로 주문을 받아서 상품 정보 조회는 필수라고 생각해서, product_id 대신에 product_no로 생각 했습니다.
예) product_id 가 없는 상품 주문이나 판매 중지 상품 등..

하지만 주문 생성을 진행하다 보니, 요구사항에 없는 부분을 고려하는 것을 빼고 
주문을 product_id로 받아서, 기일내에 요구사항부터 충족하고자 변경 했습니다. 

DB 수정 의사 결정 ( 2차 )
PurchaseOrder에 있는 totalQuantity 삭제 
사유 : 최초 설계 당시 주문 총 재고 수량을 검증하고자 추가 했지만, 불필요한 항목이라고 판단하여 삭제 했습니다.
purchase_order_item 에 있는 inventory_no 를 not null 에서 null 로 수정
사유 : 상품 재고 조회 실패한 경우도 저장을 할 수 있는데, 최초 생성할 때 not null 로 생성해서 수정 했습니다.