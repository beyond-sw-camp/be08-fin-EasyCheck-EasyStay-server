package com.beyond.easycheck.rooms.ui.requestbody;

import com.beyond.easycheck.rooms.infrastructure.entity.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RoomUpdateRequest {

    @NotBlank
    private String roomNumber;

    @NotBlank
    private String roomPic;

    @NotBlank
    private RoomStatus status;

}
