package com.home.trip.domain;

import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.domain.enums.Badge;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String guestToken;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
    @Builder.Default
    private List<SurveyAnswer> surveyAnswers = new ArrayList<>();

    @OneToOne(mappedBy = "survey", cascade = CascadeType.ALL)
    private TripRecommendation tripRecommendation;

    @Enumerated(value = EnumType.STRING)
    private Badge badge;

    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * 답변 편의 메서드(주인이 아닌쪽)
     *
     * @param answer 설문 질문에 대한 답변
     */
    public void addAnswer(SurveyAnswer answer) {
        surveyAnswers.add(answer);
        answer.confirmSurvey(this); // 양방향 관계 세팅
    }

    /**
     * 추천 편의 메서드(주인이 아닌쪽)
     *
     * @param tripRecommendation 여행 추천
     */
    public void setTripRecommendation(TripRecommendation tripRecommendation) {
        this.tripRecommendation = tripRecommendation;
        tripRecommendation.confirmSurvey(this); // 양방향 관계 세팅
    }

    /**
     * DTO로 Survey Entity 생성(설문에 대한 답변 제외)
     *
     * @param dto 설문에 대한 답변 및 회원 정보 등
     * @return 회원 정보, 만든 날짜만 포함된 Survey
     */
    public static Survey createSurvey(SurveyDto dto) {
        return Survey.builder()
                .user(dto.getUser())
                .guestToken(dto.getGuestToken())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
