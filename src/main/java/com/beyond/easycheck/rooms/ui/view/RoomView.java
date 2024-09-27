package com.beyond.easycheck.rooms.ui.view;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomView {

    private Long roomId;

    private Long roomTypeId;

    private String roomNumber;

    private String roomPic;

    private RoomStatus status;

//    public static RoomView of(RoomEntity roomEntity) {
//        return new RoomView(
//                roomEntity.getRoomId(),
//                roomEntity.getRoomTypeId(),
//                roomEntity.getRoomNumber(),
//                roomEntity.getRoomPic(),
//                roomEntity.getStatus()
//        );
//    }
}
