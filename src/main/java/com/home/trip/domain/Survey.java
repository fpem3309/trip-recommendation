package com.home.trip.domain;

import com.home.trip.domain.dto.SurveyDto;
import com.home.trip.domain.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @OneToOne(mappedBy = "survey")
    private TripRecommendation tripRecommendation;

    private String guestToken;

    @Enumerated(EnumType.STRING)
    private TripType tripType;

    private String period;
    private String peopleCount;
    private String budget;

    @Enumerated(EnumType.STRING)
    private PreferenceType preferenceType;

    @Enumerated(EnumType.STRING)
    private Transportation transportation;

    @Enumerated(EnumType.STRING)
    private TripStyle tripStyle;
    private int foodImportance;

    @Enumerated(EnumType.STRING)
    private Accommodation accommodation;

    @Enumerated(EnumType.STRING)
    private Companion companion;

    @CreatedDate
    private LocalDateTime createdAt;

    public static Survey createSurvey(SurveyDto dto) {
        return Survey.builder()
                .guestToken(dto.getGuestToken())
                .tripType(dto.getTripType())
                .period(dto.getPeriod())
                .peopleCount(dto.getPeopleCount())
                .budget(dto.getBudget())
                .preferenceType(dto.getPreferenceType())
                .transportation(dto.getTransportation())
                .tripStyle(dto.getTripStyle())
                .foodImportance(dto.getFoodImportance())
                .accommodation(dto.getAccommodation())
                .companion(dto.getCompanion())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
