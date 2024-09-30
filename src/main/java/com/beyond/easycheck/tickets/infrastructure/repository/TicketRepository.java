package com.beyond.easycheck.tickets.infrastructure.repository;

import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    List<TicketEntity> findByThemeParkId(Long themeParkId);

    boolean existsByTicketName(String ticketName);
}
