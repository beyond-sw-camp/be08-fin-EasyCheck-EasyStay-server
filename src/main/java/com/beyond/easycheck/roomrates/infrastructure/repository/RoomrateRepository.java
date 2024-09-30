package com.beyond.easycheck.roomrates.infrastructure.repository;

import com.beyond.easycheck.roomrates.infrastructure.entity.RoomrateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomrateRepository extends JpaRepository<RoomrateEntity, Long> {
}
