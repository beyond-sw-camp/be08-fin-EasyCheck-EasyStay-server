package com.beyond.easycheck.rooms.infrastructure.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;
import com.beyond.easycheck.rooms.ui.requestbody.RoomUpdateRequest;
import com.beyond.easycheck.roomtypes.infrastructure.entity.RoomtypeEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "room")
@NoArgsConstructor
@AllArgsConstructor
public class RoomEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @ManyToOne
    @JoinColumn(name = "room_type_id", nullable = false)
    @JsonManagedReference
    private RoomtypeEntity roomTypeEntity;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String roomPic;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Column(nullable = false)
    private int roomAmount;

    @Column(nullable = false)
    private int remainingRoom;

    @PrePersist
    public void prePersist() {
        this.remainingRoom = this.roomAmount;
    }

    public void update(RoomUpdateRequest roomUpdateRequest) {
        roomNumber = roomUpdateRequest.getRoomNumber();
        roomPic = roomUpdateRequest.getRoomPic();
        roomAmount = roomUpdateRequest.getRoomAmount();
        status = roomUpdateRequest.getStatus();
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public void setRemainingRoom(int remainingRoom) {
        this.remainingRoom = remainingRoom;
    }
}
