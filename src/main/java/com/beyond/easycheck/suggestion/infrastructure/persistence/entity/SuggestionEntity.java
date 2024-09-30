package com.beyond.easycheck.suggestion.infrastructure.persistence.entity;

import com.beyond.easycheck.accomodations.infrastructure.entity.AccommodationEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Suggestions")
public class SuggestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "accommodation_id", nullable = false)
    @JsonManagedReference
    private AccommodationEntity accommodationEntity;

    // suggestion_type : 유형
    @NotNull
    @Column(length = 20)
    private String type;

    // 주제
    @NotNull
    @Column(length = 20)
    private String subject;

    // suggester_name : 건의자명
    @NotNull
    @Column(length = 10)
    private String suggesterName;


    @NotNull
    @Column(length = 30)
    private String email;

    @NotNull
    @Column(length = 100)
    private String title;

    @NotNull
    @Column(length = 500)
    private String content;


    // attachment_path : 파일 경로
    private String attachmentPath;


    @NotNull
    @Enumerated(EnumType.STRING)
    private AgreementType agreementType;


}
