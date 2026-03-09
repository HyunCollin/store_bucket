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