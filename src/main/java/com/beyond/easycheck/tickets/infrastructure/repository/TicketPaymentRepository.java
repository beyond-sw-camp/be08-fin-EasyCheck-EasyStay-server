package com.beyond.easycheck.tickets.infrastructure.repository;


import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketPaymentRepository extends JpaRepository<TicketPaymentEntity, Long> {
    Optional<TicketPaymentEntity> findByTicketOrderId(Long ticketOrderId);
}