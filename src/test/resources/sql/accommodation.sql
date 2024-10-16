DROP TABLE IF EXISTS Accommodation CASCADE;

-- Accommodation 테이블 생성
CREATE TABLE Accommodation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    accommodation_type VARCHAR(50) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO accommodation (name, address, accommodation_type)
VALUES ('선셋 리조트', '123 해변로, 오션 시티', 'RESORT'),
       ('마운틴 뷰 호텔', '456 언덕길, 알파인 타운', 'HOTEL'),
       ('파라다이스 리조트', '789 섬길, 파라다이스 섬', 'RESORT'),
       ('시티 센트럴 호텔', '101 다운타운 거리, 메트로폴리스', 'HOTEL'),
       ('시사이드 리조트', '102 해안로, 쇼어라인', 'RESORT'),
       ('어반 플라자 호텔', '103 비즈니스 거리, 메트로폴리스', 'HOTEL'),
       ('레이크사이드 리조트', '104 강변로, 레이크빌', 'RESORT'),
       ('그랜드 팰리스 호텔', '105 럭셔리 대로, 캐피탈 시티', 'HOTEL'),
       ('씨더 포레스트 리조트', '106 숲길, 포레스트빌', 'RESORT'),
       ('스카이라인 호텔', '107 스카이라인 거리, 어바니아', 'HOTEL');

