package com.beyond.easycheck.infrastructure.persistence.entity;

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

    @NotNull
    @Column(length = 20)
    private String type;
    // suggestion_type : 유형

    @NotNull
    @Column(length = 20)
    private String subject;
    // 주제

    @NotNull
    @Column(length = 10)
    private String name;
    // suggester_name : 건의자명

    @NotNull
    @Column(length = 30)
    private String email;

    @NotNull
    @Column(length = 100)
    private String title;

    @NotNull
    @Column(length = 500)
    private String content;



    private String url;
    // attachment_path : 파일 경로

    @NotNull
    @Enumerated(EnumType.STRING)
    private AgreementType agreementType;


}
