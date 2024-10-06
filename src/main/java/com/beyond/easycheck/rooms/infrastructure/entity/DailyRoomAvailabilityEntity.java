package com.beyond.easycheck.rooms.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
@Table(name = "daily_room_availability")
public class DailyRoomAvailabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @Column(nullable = false)
    private LocalDateTime date; // 해당 날짜

    @Column(nullable = false)
    private int remainingRoom = 10; // 기본값을 10으로 설정

    public void decrementRemainingRoom() {
        if (remainingRoom > 0) {
            remainingRoom--;
        }
    }

    public void incrementRemainingRoom() {
        if (remainingRoom < 10) {
            remainingRoom++;
        }
    }
}
