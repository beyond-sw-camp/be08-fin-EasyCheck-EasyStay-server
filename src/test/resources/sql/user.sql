
-- 유저 역할 더미
insert into role(name)
values ('USER'),
       ('ADMIN'),
       ('GUEST');

-- 유저 더비
INSERT INTO users (email, password, name, phone, addr, addr_detail, point, marketing_consent, role_id)
VALUES
    -- 일반 유저
    ('john.doe@example.com', 'hashed_password_123', 'John Doe', '010-1234-5678', '서울시 강남구', '테헤란로 123', 1000, 'N',
     (SELECT id FROM role WHERE name = 'USER')),
    ('jane.smith@example.com', 'hashed_password_456', 'Jane Smith', '010-2345-6789', '서울시 서초구', '반포대로 456', 1500, 'N',
     (SELECT id FROM role WHERE name = 'USER')),
    ('kim.minsoo@example.com', 'hashed_password_789', '김민수', '010-3456-7890', '부산시 해운대구', '해운대로 789', 500, 'N',
     (SELECT id FROM role WHERE name = 'USER')),

    -- 관리자 유저
    ('admin.lee@example.com', 'admin_hashed_pw_123', '이관리', '010-7890-1234', '서울시 종로구', '종로 1길 10', 5000, 'N',
     (SELECT id FROM role WHERE name = 'ADMIN')),
    ('super.admin@example.com', 'super_admin_hashed_pw', 'Super Admin', '010-8901-2345', '서울시 중구', '세종대로 100', 10000, 'N',
     (SELECT id FROM role WHERE name = 'ADMIN'));


INSERT INTO permission(name, description)
VALUES ('ADMIN_MANAGER', '관리자 관리 가능한 권한');
