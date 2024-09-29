package com.beyond.easycheck.notices.infrastructure.persistence.entity;

import com.beyond.easycheck.common.entity.BaseTimeEntity;

import com.beyond.easycheck.notices.ui.requestbody.NoticesUpdateRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Notices")
public class NoticesEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 100)
    private String title;

    @NotNull
    @Column(length = 500)
    private String content;


    public void updateNotices(NoticesUpdateRequest noticesUpdateRequest) {

        Optional.ofNullable(noticesUpdateRequest.getTitle()).ifPresent(title -> this.title = title);
        Optional.ofNullable(noticesUpdateRequest.getContent()).ifPresent(content -> this.content = content);

    }
}
