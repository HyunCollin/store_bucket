# store
과제 진행 현황
1. 주문 API [POST] /order  - 구현 완료
2. 취소 API [PUT] /order/{orderId}/cancel 
   1. 구현  [전체 취소, 부분취소 완료, 취소 성공 이력 관리]
   2. 미구현 [취소 실패 이력 관리]
3. 주문 내역 조회 [GET] /order - 구현 완료
4. 어드민 API
   1. 기록 조회 - 미구현
   2. 재고 모니터링 - 구현 완료 [GET] /api/admin/quantity/history

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
------------------------------- 취소 API 개발 과정에서의 DB 및 API 수정 의사 결정 -------------------------------
부분취소, 전체취소, 취소이력 관리를 부분 완료 했습니다.
현재 취소 성공에 대한 이력만 관리되며, 실패에 대한 이력관리는 되지 않습니다.
설계 당시 고려는 했지만, 작업하면서 취소 실패와 성공을 어떻게 구분 관리할 지에 대한 고민이 생겨서 TODO로 남겨두고 진행 했습니다.
------------------------------- JWT, 인증 권한 설정 -------------------------------
토큰 설계 구조 및 상세 내용

1. 토큰 개요
   JWT(JSON Web Token) 라이브러리 중 JJWT를 사용했습니다.
2. 토큰 상세 사양
* 암호화 알고리즘: HMAC-SHA256 (HS256)
* 토큰 유효 기간: 발급 시점으로부터 10분
* 토큰 전달 방식: HTTP Authorization 헤더에 Bearer 토큰 형식으로 포함
* 비밀키 관리: 서버 내 환경변수(jwt.secret)를 통해 관리

3. 토큰 내부 구조 (Payload)
* Subject: 사용자의 고유 ID (userId)
* Claims: 사용자의 권한 정보 (roles: ROLE_USER 또는 ROLE_ADMIN)
* Expiration: 토큰의 만료 시간 정보

4. 인증 및 인가 프로세스
* 발급: 사용자가 ID를 통해 발급 API를 호출하면 관리자 여부를 확인 하고, 권한에 맞는 서명된 토큰을 생성 및 반환합니다.
* 필터링: 모든 API 요청 시 JwtAuthenticationFilter 에서 토큰을 검증합니다.
* 검증: TokenService가 서명의 위변조 여부와 만료 시간을 체크하며, 문제가 있을 경우 401 에러를 반환합니다.
* 권한 부여: 검증이 완료된 정보는 SecurityContext에 저장되어 컨트롤러에서 유저 ID를 사용할 수 있게 하며, 설정된 역할에 따라 API 접근을 제한합니다.

5. 권한 구분
* 일반 사용자 권한: 주문 생성, 주문 취소, 주문 내역 조회, Token 발급 API 접근 가능
* 관리자 권한: 일반 사용자 권한, 시스템 로그 조회, 실시간 재고 모니터링 등 관리 기능을 포함한 모든 API 접근 가능
  ------------------------------- 어드민 API -------------------------------
* 재고 모니터링 : 모든 상품의 현재 잔여 재고와 마지막 유효 주문 일시 확인
구현 과정
product_inventory_history 에 유효한 주문이 완료 될 때 마다 최종 주문번호와 주문 유형을 저장하도록 구현 했습니다.
actionType ORDER(구매), CANCEL(취소)로 구분하여 모두 저장 했습니다.
API로 조회 할 때 actionType을 파라미터로 받아서, 유효 주문 구분 값을 선택 할 수 있도록 했습니다.
