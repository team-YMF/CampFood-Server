package com.campfood.src.store.entity;

import com.campfood.common.entity.BaseEntity;
import com.campfood.src.store.dto.request.StoreUpdateDTO;
import com.campfood.src.university.entity.University;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Where(clause = "is_deleted = false")
@DynamicInsert
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identificationId;

    @Column(nullable = false)
    private String name;

    private String image;

    private String description;

    @ColumnDefault("0.0")
    private Double naverRate;

    @ColumnDefault("0")
    private int naverVisitedReviewCnt;

    @ColumnDefault("0")
    private int naverBlogReviewCnt;

    @ColumnDefault("0.0")
    private Double campFoodRate;

    @ColumnDefault("0")
    private int campFoodReviewCnt;

    @Column(nullable = false)
    private String address;

    private String latitude;
    private String longitude;

    private String storeNumber;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StoreCategory> storeCategories = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StoreOpenTime> storeOpenTimes = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StoreUniversity> universities = new ArrayList<>();

    public void updateStore(StoreUpdateDTO storeUpdateDTO) {
        this.name = storeUpdateDTO.getName();
        this.naverRate = storeUpdateDTO.getRate();
        this.naverVisitedReviewCnt = storeUpdateDTO.getVisitedReview();
        this.naverBlogReviewCnt = storeUpdateDTO.getBlogReview();
        this.address = storeUpdateDTO.getAddress();
        this.storeNumber = storeUpdateDTO.getStoreNumber();
        this.latitude = storeUpdateDTO.getLatitude();
        this.longitude = storeUpdateDTO.getLongitude();
    }

    public void updateCategories(List<StoreCategory> categories) {
        this.storeCategories = categories;
    }

    public void updateOpenTimes(List<StoreOpenTime> openTimes) {
        this.storeOpenTimes = openTimes;
    }

    public void addUniversity(StoreUniversity storeUniversity) {
        this.universities.add(storeUniversity);
    }
}
