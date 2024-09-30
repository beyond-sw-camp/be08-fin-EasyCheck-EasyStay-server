package com.beyond.easycheck.notices.ui.view;

import com.beyond.easycheck.additionalservices.infrastructure.entity.AdditionalServiceEntity;
import com.beyond.easycheck.additionalservices.ui.view.AdditionalServiceView;
import com.beyond.easycheck.notices.infrastructure.persistence.entity.NoticesEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NoticesView {

    private Long id;

    private String accommodationName;

    private String title;

    private String content;

    public static List<NoticesView> listof(List<NoticesEntity> filteredNotices) {

        return filteredNotices.stream()
                .map(NoticesView::of)
                .toList();
    }

    public static NoticesView of(NoticesEntity noticesEntity) {

        return new NoticesView(
                noticesEntity.getId(),
                noticesEntity.getAccommodationEntity().getName(),
                noticesEntity.getTitle(),
                noticesEntity.getContent());
    }
}
