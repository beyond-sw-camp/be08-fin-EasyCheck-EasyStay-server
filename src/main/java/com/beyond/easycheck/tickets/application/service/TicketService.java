package com.beyond.easycheck.tickets.application.service;

import com.beyond.easycheck.common.exception.EasyCheckException;
import com.beyond.easycheck.adasfas.infrastructure.entity.ThemeParkEntity;
import com.beyond.easycheck.adasfas.infrastructure.repository.ThemeParkRepository;
import com.beyond.easycheck.tickets.exception.TicketMessageType;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import com.beyond.easycheck.tickets.infrastructure.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.beyond.easycheck.adasfas.exception.ThemeParkMessageType.THEME_PARK_NOT_FOUND;
import static com.beyond.easycheck.tickets.exception.TicketMessageType.TICKET_NOT_BELONG_TO_THEME_PARK;
import static com.beyond.easycheck.tickets.exception.TicketMessageType.TICKET_NOT_FOUND;
import static com.beyond.easycheck.tickets.exception.TicketMessageType.DUPLICATE_TICKET;
import static com.beyond.easycheck.tickets.exception.TicketMessageType.MISSING_REQUIRED_FIELD;

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

        if (command.getTicketName() == null || command.getPrice() == null) {
            throw new EasyCheckException(MISSING_REQUIRED_FIELD);
        }

        if (ticketRepository.existsByTicketName(command.getTicketName())) {
            throw new EasyCheckException(DUPLICATE_TICKET);
        }

        TicketEntity ticket = TicketEntity.createTicket(command, themePark);
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public TicketEntity updateTicket(Long themeParkId, Long ticketId, TicketUpdateCommand command) {
        ThemeParkEntity themePark = themeParkRepository.findById(themeParkId)
                .orElseThrow(() -> new EasyCheckException(THEME_PARK_NOT_FOUND));

        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EasyCheckException(TICKET_NOT_FOUND));

        if (!ticket.getThemePark().getId().equals(themePark.getId())) {
            throw new EasyCheckException(TICKET_NOT_BELONG_TO_THEME_PARK);
        }

        ticket.update(
                command.getTicketName(),
                command.getPrice(),
                command.getSaleStartDate(),
                command.getSaleEndDate(),
                command.getValidFromDate(),
                command.getValidToDate()
        );

        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void deleteTicket(Long themeParkId, Long ticketId) {
        ThemeParkEntity themePark = themeParkRepository.findById(themeParkId)
                .orElseThrow(() -> new EasyCheckException(THEME_PARK_NOT_FOUND));

        TicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EasyCheckException(TICKET_NOT_FOUND));

        if (!ticket.getThemePark().getId().equals(themePark.getId())) {
            throw new EasyCheckException(TICKET_NOT_BELONG_TO_THEME_PARK);
        }

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
