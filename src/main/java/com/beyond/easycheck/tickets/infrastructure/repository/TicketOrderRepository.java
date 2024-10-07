package com.beyond.easycheck.tickets.infrastructure.repository;


import com.beyond.easycheck.tickets.infrastructure.entity.TicketOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketOrderRepository extends JpaRepository<TicketOrderEntity, Long> {
    List<TicketOrderEntity> findByUserId(Long userId);
}