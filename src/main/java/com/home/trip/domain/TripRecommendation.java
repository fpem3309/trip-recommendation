package com.home.trip.domain;

import com.home.trip.domain.dto.openai.RecommendDto;
import com.home.trip.domain.enums.Badge;
import com.home.trip.domain.enums.RecommendationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripRecommendation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_survey_id")
    private Survey survey;

    @Enumerated(EnumType.STRING)
    private Badge badge;

    private String city;
    private String country;
    private String tripType;
    private String period;
    private String recommendation;

    @OneToMany(mappedBy = "tripRecommendation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // ❗️Builder로 객체를 생성하면 itinerary는 null 상태가 됨
    private List<Itinerary> itinerary = new ArrayList<>();

    private String estimatedBudget;
    private String bestSeason;

    @Enumerated(EnumType.STRING)
    private RecommendationStatus status;

    /**
     * setter 대신 관계 주인이 set
     * @param survey 설문
     */
    protected void confirmSurvey(Survey survey) {
        this.survey = survey;
    }

    /**
     * Survey로 TripRecommendation Entity 생성(추천 상세 내용 제외)
     * @param survey 설문 정보
     * @return 설문 정보, 상태(PENDING)만 포함된 TripRecommendation
     */
    public static TripRecommendation createTripRecommendation(Survey survey) {
        return TripRecommendation.builder()
                .survey(survey)
                .badge(null)
                .status(RecommendationStatus.PENDING)
                .build();
    }

    /**
     * 답변 편의 메서드(주인이 아닌쪽)
     * @param item 추천 일자별 여행 일정
     */
    public void addItinerary(Itinerary item) {
        itinerary.add(item);
        item.confirmTripRecommendation(this);
    }

    public void setRecommendationTrip(RecommendDto recommendDto) {
        this.city = recommendDto.getCity();
        this.country = recommendDto.getCountry();
        this.tripType = recommendDto.getTripType();
        this.period = recommendDto.getPeriod();
        this.recommendation = recommendDto.getRecommendation();
        this.estimatedBudget = recommendDto.getEstimatedBudget();
        this.bestSeason = recommendDto.getBestSeason();
        this.status = RecommendationStatus.COMPLETED;
    }

}
