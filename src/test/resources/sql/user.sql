
-- 유저 역할 더미
insert into role(name)
values ('USER'),
       ('ADMIN'),
       ('GUEST');

-- 유저 더비
-- 원본 비밀번호 password123으로 통일
INSERT INTO users (email, password, name, phone, addr, addr_detail, point, marketing_consent, role_id, status)
VALUES
    -- 일반 유저
    ('john.doe@example.com', '$2a$10$2o44udz8WSc1mWL7dYOql.lSZ5TI.aW4h4W/UQt1gdIRPuk2Je1nO', 'John Doe', '010-1234-5678', '서울시 강남구', '테헤란로 123', 1000, 'N',
     (SELECT id FROM role WHERE name = 'USER'), 'ACTIVE'),
    ('jane.smith@example.com', '$2a$10$2o44udz8WSc1mWL7dYOql.lSZ5TI.aW4h4W/UQt1gdIRPuk2Je1nO', 'Jane Smith', '010-2345-6789', '서울시 서초구', '반포대로 456', 1500, 'N',
     (SELECT id FROM role WHERE name = 'USER'), 'ACTIVE'),
    ('kim.minsoo@example.com', '$2a$10$2o44udz8WSc1mWL7dYOql.lSZ5TI.aW4h4W/UQt1gdIRPuk2Je1nO', '김민수', '010-3456-7890', '부산시 해운대구', '해운대로 789', 500, 'N',
     (SELECT id FROM role WHERE name = 'USER'), 'ACTIVE'),

    -- 관리자 유저
    ('admin.lee@example.com', '$2a$10$2o44udz8WSc1mWL7dYOql.lSZ5TI.aW4h4W/UQt1gdIRPuk2Je1nO', '이관리', '010-7890-1234', '서울시 종로구', '종로 1길 10', 5000, 'N',
     (SELECT id FROM role WHERE name = 'ADMIN'), 'ACTIVE'),
    ('super.admin@example.com', '$2a$10$2o44udz8WSc1mWL7dYOql.lSZ5TI.aW4h4W/UQt1gdIRPuk2Je1nO', 'Super Admin', '010-8901-2345', '서울시 중구', '세종대로 100', 10000, 'N',
     (SELECT id FROM role WHERE name = 'ADMIN'), 'ACTIVE');

INSERT INTO permission(name, description)
VALUES ('ADMIN_MANAGER', '관리자 관리 가능한 권한'),
       ('THEME_PARK_CREATE', '테마파크 생성 권한'),
       ('THEME_PARK_EDIT', '테마파크 수정 권한'),
       ('THEME_PARK_DELETE', '테마파크 삭제 권한'),
       ('HOTEL_REGISTER', '호텔 등록 권한'),
       ('HOTEL_EDIT', '호텔 정보 수정 권한'),
       ('HOTEL_DELETE', '호텔 삭제 권한'),
       ('RESORT_REGISTER', '리조트 등록 권한'),
       ('RESORT_EDIT', '리조트 정보 수정 권한'),
       ('RESORT_DELETE', '리조트 삭제 권한'),
       ('ATTRACTION_MANAGE', '어트랙션 관리 권한'),
       ('EVENT_MANAGE', '이벤트 관리 권한'),
       ('RESERVATION_MANAGE', '예약 관리 권한'),
       ('CUSTOMER_SERVICE', '고객 서비스 권한'),
       ('REPORT_VIEW', '보고서 조회 권한');


-- 관리자 유저에게 권한 부여
INSERT INTO user_permission (user_id, permission_id, granted_by, granted_datetime)
SELECT u.id, p.id, 'SYSTEM', CURRENT_TIMESTAMP()
FROM users u
         CROSS JOIN permission p
WHERE u.email IN ('admin.lee@example.com', 'super.admin@example.com')
  AND p.name IN (
                 'THEME_PARK_CREATE',
                 'THEME_PARK_EDIT',
                 'HOTEL_REGISTER',
                 'RESORT_REGISTER',
                 'ATTRACTION_MANAGE',
                 'EVENT_MANAGE',
                 'RESERVATION_MANAGE',
                 'CUSTOMER_SERVICE',
                 'REPORT_VIEW'
    );

-- Super Admin에게 추가 권한 부여
INSERT INTO user_permission (user_id, permission_id, granted_by, granted_datetime)
SELECT u.id, p.id, 'SYSTEM', CURRENT_TIMESTAMP()
FROM users u
         CROSS JOIN permission p
WHERE u.email = 'super.admin@example.com'
  AND p.name IN (
                 'ADMIN_MANAGER',
                 'THEME_PARK_DELETE',
                 'HOTEL_DELETE',
                 'RESORT_DELETE'
    );