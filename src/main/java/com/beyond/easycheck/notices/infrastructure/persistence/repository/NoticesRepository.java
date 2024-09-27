package com.beyond.easycheck.notices.infrastructure.persistence.repository;

import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NoticesRepository extends JpaRepository<NoticesEntity, Long> {

}
