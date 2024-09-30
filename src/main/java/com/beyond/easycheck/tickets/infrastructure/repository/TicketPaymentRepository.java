package com.beyond.easycheck.tickets.infrastructure.repository;


import com.beyond.easycheck.tickets.infrastructure.entity.TicketPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketPaymentRepository extends JpaRepository<TicketPaymentEntity, Long> {
}