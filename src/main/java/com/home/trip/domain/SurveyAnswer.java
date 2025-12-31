package com.home.trip.domain;

import com.home.trip.domain.dto.SurveyDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    private Long questionId;

    private String answer;

    /**
     * setter 대신 관계 주인이 set
     * @param survey 설문
     */
    protected void confirmSurvey(Survey survey) {
        this.survey = survey;
    }

    public static SurveyAnswer createSurveyAnswer(SurveyDto.SurveyAnswerDto dto) {
        return SurveyAnswer.builder()
                .questionId(dto.getQuestionId())
                .answer(dto.getAnswer())
                .build();
    }
}
