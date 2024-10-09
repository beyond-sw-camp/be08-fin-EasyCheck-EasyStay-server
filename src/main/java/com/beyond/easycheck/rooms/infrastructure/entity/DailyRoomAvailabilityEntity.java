package com.beyond.easycheck.rooms.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Builder
@Table(name = "daily_room_availability_entity",
        uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "date"}))
public class DailyRoomAvailabilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private int remainingRoom = 10;

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

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void setRemainingRoom(int remainingRoom) {
        this.remainingRoom = remainingRoom;
    }
}
