package com.beyond.easycheck.rooms.infrastructure.persistence.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;
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

    @Column(nullable = false)
    private Long roomTypeId;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String roomPic;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

}
