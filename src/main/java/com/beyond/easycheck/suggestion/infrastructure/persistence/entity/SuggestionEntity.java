package com.beyond.easycheck.suggestion.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String name;


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
