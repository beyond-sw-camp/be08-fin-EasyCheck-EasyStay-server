package com.beyond.easycheck.notices.ui.view;

import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NoticesView {

    private Long id;

    private String title;

    private String content;

//    public static List<NoticesView> listOf(List<NoticesEntity>) filtered


    public static NoticesView of(NoticesEntity entity) {
        return new NoticesView(
                entity.getId(),
                entity.getTitle(),
                entity.getContent());
    }
}
