-- RoomType 테이블 생성
CREATE TABLE RoomType (
    room_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    accommodation_id BIGINT NOT NULL,
    type_name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    max_occupancy INT NOT NULL,
    FOREIGN KEY (accommodation_id) REFERENCES ACCOMMODATION(accommodation_id) -- Accommodation 테이블이 존재해야 함
);

-- RoomType 더미
INSERT INTO RoomType (accommodation_id, type_name, description, max_occupancy)
VALUES (1, '디럭스', '한 명이 묵을 수 있는 아늑한 룸', 1),
       (3, '디럭스 - 원룸', '두 명이 묵을 수 있는 넓은 룸', 2),
       (5, '스위트 - 오션뷰', '프리미엄 편의 시설이 갖춰진 우아한 룸', 4),
       (7, '로얄 - 오션뷰', '최대 네 명이 묵을 수 있는 큰 룸', 4),
       (9, '로얄 - 호텔형', '별도의 거실 공간이 있는 고급스러운 룸', 5),
       (11, '플래티넘', '세련된 인테리어와 고급 편의 시설이 갖춰진 룸', 4);

SELECT * FROM ROOMTYPE WHERE room_type_id = 1;

-- CREATE TABLE room (
--                       room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
--                       room_type_id BIGINT NOT NULL,
--                       room_number VARCHAR(50) NOT NULL,
--                       room_pic VARCHAR(255) NOT NULL,
--                       status VARCHAR(20) NOT NULL,
--                       room_amount INT NOT NULL,
--                       remaining_room INT NOT NULL,
--                       FOREIGN KEY (room_type_id) REFERENCES roomtype(room_type_id) ON DELETE CASCADE
-- );