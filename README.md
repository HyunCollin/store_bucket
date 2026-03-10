# store
과제 진행 현황
1. 주문 API [POST] /order  - 구현 완료


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

------------------------------- 주문 API 개발 과정에서의 DB 및 API 수정 의사 결정 -------------------------------
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

DB 및 API 수정 의사 결정 ( 3차 )
주문 요청 할 때 inventory_no를 받는 것으로 수정
사유 : 처음에는 상품코드,컬러,사이즈를 받아서 재고에서 inventory_no를 조회해서 주문을 생성하려고 했지만,
주문 요청 시 inventory_no를 받아서 주문을 생성하는 것으로 변경 했습니다.
inventory_no를 받기로 해서 주문 옵션을 저장하려고 만든 'snapshotInfo' 도 삭제 했습니다.
FE 에서 상품을 조회 할 때 inventory_no를 같이 전달 하면, 검증과 주문 생성이 더 간편할 것 같아서 변경 했습니다.

주문 생성 프로세스 수정
처음 과제를 생각했을 때도 주문 생성 요청이 오면 
주문 요청 정보를 임시 상태로 저장 하고
주문 재고 차감과 실패에 대한 처리를 분리하려고 했습니다. 
실제 구현을 하면서 생각과 정리가 되어서 오늘 수정 했습니다.
그리고 재고차감 실패에 대한 처리를 exception 으로 처리 하는 것으로 로직을 단순화 했습니다.
------------------------------- 주문 API 개발 과정에서의 DB 및 API 수정 의사 결정 -------------------------------