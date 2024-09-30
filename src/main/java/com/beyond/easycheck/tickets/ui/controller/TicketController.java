package com.beyond.easycheck.tickets.ui.controller;

import com.beyond.easycheck.common.ui.view.ApiResponseView;
import com.beyond.easycheck.tickets.application.service.TicketOperationUseCase;
import com.beyond.easycheck.tickets.application.service.TicketOperationUseCase.TicketCreateCommand;
import com.beyond.easycheck.tickets.application.service.TicketOperationUseCase.TicketUpdateCommand;
import com.beyond.easycheck.tickets.application.service.TicketReadUseCase;
import com.beyond.easycheck.tickets.application.service.TicketReadUseCase.FindTicketResult;
import com.beyond.easycheck.tickets.infrastructure.entity.TicketEntity;
import com.beyond.easycheck.tickets.ui.requestbody.TicketRequest;
import com.beyond.easycheck.tickets.ui.view.TicketView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parks/{themeParkId}/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketOperationUseCase ticketOperationUseCase;
    private final TicketReadUseCase ticketReadUseCase;

    @PostMapping("")
    public ResponseEntity<ApiResponseView<TicketView>> createTicket(@PathVariable Long themeParkId,
                                                                    @RequestBody TicketRequest request) {
        TicketCreateCommand command = TicketCreateCommand.builder()
                .themeParkId(themeParkId)
                .ticketName(request.getTicketName())
                .price(request.getPrice())
                .saleStartDate(request.getSaleStartDate())
                .saleEndDate(request.getSaleEndDate())
                .validFromDate(request.getValidFromDate())
                .validToDate(request.getValidToDate())
                .build();

        TicketEntity ticket = ticketOperationUseCase.createTicket(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponseView<>(new TicketView(ticket)));
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<ApiResponseView<TicketView>> updateTicket(@PathVariable Long themeParkId,
                                                                    @PathVariable Long ticketId,
                                                                    @RequestBody TicketRequest request) {

        TicketUpdateCommand command = TicketUpdateCommand.builder()
                .ticketName(request.getTicketName())
                .price(request.getPrice())
                .saleStartDate(request.getSaleStartDate())
                .saleEndDate(request.getSaleEndDate())
                .validFromDate(request.getValidFromDate())
                .validToDate(request.getValidToDate())
                .build();

        TicketEntity updatedTicket = ticketOperationUseCase.updateTicket(ticketId, command);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new TicketView(updatedTicket)));
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long themeParkId, @PathVariable Long ticketId) {

        ticketOperationUseCase.deleteTicket(ticketId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("")
    public ResponseEntity<ApiResponseView<List<TicketView>>> getAllTicketsByThemePark(@PathVariable Long themeParkId) {
        List<FindTicketResult> results = ticketReadUseCase.getTicketsByThemePark(themeParkId);

        List<TicketView> ticketViews = results.stream()
                .map(TicketView::new)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(ticketViews));
    }

    @GetMapping("/on-sale")
    public ResponseEntity<ApiResponseView<List<TicketView>>> getTicketsByThemeParkOnSale(@PathVariable Long themeParkId) {
        List<FindTicketResult> results = ticketReadUseCase.getTicketsByThemeParkOnSale(themeParkId);

        List<TicketView> ticketViews = results.stream()
                .map(TicketView::new)
                .toList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(ticketViews));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponseView<TicketView>> getTicketById(@PathVariable Long ticketId) {
        FindTicketResult result = ticketReadUseCase.getTicketById(ticketId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseView<>(new TicketView(result)));
    }
}
