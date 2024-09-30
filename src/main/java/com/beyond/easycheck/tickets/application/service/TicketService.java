package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.themeparks.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.themeparks.infrastructure.repository.ThemeParkRepository;
import com.beyond.easycheck.tickets.exception.TicketMessageType;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.themeparks.exception.ThemeParkMessageType.THEME_PARK_NOT_FOUND;
import static com.beyond.easycheck.tickets.exception.TicketMessageType.TICKET_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService implements TicketOperationUseCase, TicketReadUseCase {

    private final TicketRepository ticketRepository;
    private final ThemeParkRepository themeParkRepository;

    @Override
    @Transactional
    public TicketEntity createTicket(TicketCreateCommand command) {
        ThemeParkEntity themePark = themeParkRepository.findById(command.getThemeParkId())
                .orElseThrow(() -> new EasyCheckException(THEME_PARK_NOT_FOUND));

        TicketEntity ticket = TicketEntity.createTicket(command, themePark);

        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public TicketEntity updateTicket(Long ticketId, TicketUpdateCommand command) {

        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EasyCheckException(TICKET_NOT_FOUND));

        ticket.update(
                command.getTicketName(),
                command.getPrice(),
                command.getSaleStartDate(),
                command.getSaleEndDate(),
                command.getValidFromDate(),
                command.getValidToDate()
        );

        return ticket;
    }

    @Override
    @Transactional
    public void deleteTicket(Long ticketId) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EasyCheckException(TICKET_NOT_FOUND));

        ticketRepository.delete(ticket);
    }

    @Override
    public List<FindTicketResult> getTicketsByThemePark(Long themeParkId) {
        List<TicketEntity> tickets = ticketRepository.findByThemeParkId(themeParkId);

        return tickets.stream()
                .map(FindTicketResult::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<FindTicketResult> getTicketsByThemeParkOnSale(Long themeParkId) {
        LocalDateTime now = LocalDateTime.now();
        List<TicketEntity> tickets = ticketRepository.findByThemeParkId(themeParkId);

        return tickets.stream()
                .filter(ticket -> !ticket.getSaleStartDate().isAfter(now) && !ticket.getSaleEndDate().isBefore(now))
                .map(FindTicketResult::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public FindTicketResult getTicketById(Long ticketId) {
        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EasyCheckException(TicketMessageType.TICKET_NOT_FOUND));

        return FindTicketResult.fromEntity(ticket);
    }
}
