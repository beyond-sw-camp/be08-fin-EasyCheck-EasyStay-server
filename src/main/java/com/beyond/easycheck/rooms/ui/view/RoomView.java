package com.beyond.easycheck.rooms.ui.view;

import com.beyond.easycheck.rooms.infrastructure.persistence.entity.RoomEntity;
import com.beyond.easycheck.rooms.infrastructure.persistence.entity.RoomStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomView {

    private Long roomId;

    private Long roomTypeId;

    private String roomNumber;

    private String roomPic;

    private RoomStatus status;

    public static RoomView of(RoomEntity roomEntity) {
        return new RoomView(
                roomEntity.getRoomId(),
                roomEntity.getRoomTypeId(),
                roomEntity.getRoomNumber(),
                roomEntity.getRoomPic(),
                roomEntity.getStatus()
        );
    }
}
