-- RoomType 테이블 생성
CREATE TABLE RoomType (
    room_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    accommodation_id BIGINT NOT NULL,
    type_name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    max_occupancy INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATION(id)
);

-- RoomType 더미
INSERT INTO RoomType (accommodation_id, type_name, description, max_occupancy)
VALUES (1, '디럭스', '한 명이 묵을 수 있는 아늑한 룸', 1),
       (2, '디럭스 - 원룸', '두 명이 묵을 수 있는 넓은 룸', 2),
       (3, '스위트 - 오션뷰', '프리미엄 편의 시설이 갖춰진 우아한 룸', 4),
       (4, '로얄 - 오션뷰', '최대 네 명이 묵을 수 있는 큰 룸', 4),
       (5, '로얄 - 호텔형', '별도의 거실 공간이 있는 고급스러운 룸', 5),
       (6, '플래티넘', '세련된 인테리어와 고급 편의 시설이 갖춰진 룸', 4);
