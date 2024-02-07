package com.campfood.src.store.mapper;

import com.campfood.src.member.entity.Member;
import com.campfood.src.store.dto.request.StoreUpdateDTO;
import com.campfood.src.store.dto.response.StoreInquiryAllDTO;
import com.campfood.src.store.dto.response.StoreInquiryDetailDTO;
import com.campfood.src.store.dto.response.StoreInquiryPopularDTO;
import com.campfood.src.store.dto.response.StoreSearchByKeywordDTO;
import com.campfood.src.store.entity.*;
import com.campfood.src.university.entity.University;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StoreMapper {
    public Store toStore(StoreUpdateDTO request) {
        return Store.builder()
                .identificationId(request.getIdentificationId())
                .name(request.getName())
                .naverRate(request.getRate())
                .naverVisitedReviewCnt(request.getVisitedReview())
                .naverBlogReviewCnt(request.getBlogReview())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

    }

    public StoreCategory toStoreCategory(Category category, Store store) {
        return StoreCategory.builder()
                .store(store)
                .category(category)
                .build();
    }

    public StoreOpenTime toStoreOpenTime(StoreUpdateDTO.OpeningTime openingTime, Store store) {
        return StoreOpenTime.builder()
                .store(store)
                .day(openingTime.getDayOfWeek())
                .content(openingTime.getContent())
                .build();
    }

    public StoreUniversity toStoreUniversity(University university, Store store) {
        return StoreUniversity.builder()
                .store(store)
                .university(university)
                .build();
    }

    public StoreHeart toStoreHeart(Member member, Store store) {
        return StoreHeart.builder()
                .store(store)
                .member(member)
                .isChecked(false)
                .build();
    }

    public StoreInquiryAllDTO toInquiryByTagDTO(Store store) {

        return StoreInquiryAllDTO.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .storeCategories(toTags(store.getStoreCategories()))
                .storeImage(store.getImage())
                .naverRate(store.getNaverRate())
                .naverVisitedReviewCnt(store.getNaverVisitedReviewCnt())
                .naverBlogReviewCnt(store.getNaverBlogReviewCnt())
                .campFoodRate(store.getCampFoodRate())
                .camFoodReviewCnt(store.getCampFoodReviewCnt())
                .build();
    }

    public StoreInquiryDetailDTO toInquiryDetailDTO(Store store) {

        List<StoreInquiryDetailDTO.OpenTimeInfo> openTimeInfos = store.getStoreOpenTimes().stream()
                .map(this::toOpenTimeInfo)
                .collect(Collectors.toList());

        return StoreInquiryDetailDTO.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .storeCategories(toTags(store.getStoreCategories()))
                .storeImage(store.getImage())
                .naverRate(store.getNaverRate())
                .naverVisitedReviewCnt(store.getNaverVisitedReviewCnt())
                .naverBlogReviewCnt(store.getNaverBlogReviewCnt())
                .campFoodRate(store.getCampFoodRate())
                .camFoodReviewCnt(store.getCampFoodReviewCnt())
                .storeAddress(store.getAddress())
                .openTimeInfos(openTimeInfos)
                .storeNumber(store.getStoreNumber())
                .build();
    }

    public StoreSearchByKeywordDTO toSearchByKeywordDTO(Store store) {
        return StoreSearchByKeywordDTO.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .storeCategories(toTags(store.getStoreCategories()))
                .storeImage(store.getImage())
                .campFoodRate(store.getCampFoodRate())
                .campFoodReviewCnt(store.getCampFoodReviewCnt())
                .build();
    }

    public StoreInquiryPopularDTO toInquiryByPopularDTO(Store store) {
        return StoreInquiryPopularDTO.builder()
                .storeId(store.getId())
                .storeImage(store.getImage())
                .storeCategory(toTags(store.getStoreCategories()).get(0))
                .build();
    }

    private List<Category> toTags(List<StoreCategory> storeCategories) {
        return storeCategories.stream()
                .map(StoreCategory::getCategory)
                .collect(Collectors.toList());
    }

    private StoreInquiryDetailDTO.OpenTimeInfo toOpenTimeInfo(StoreOpenTime storeOpenTime) {
        return StoreInquiryDetailDTO.OpenTimeInfo.builder()
                .day(storeOpenTime.getDay())
                .content(storeOpenTime.getContent())
                .build();
    }
}
