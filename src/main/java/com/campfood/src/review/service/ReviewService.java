package com.campfood.src.review.service;

import com.campfood.common.error.ErrorCode;
import com.campfood.common.exception.RestApiException;
import com.campfood.common.service.EntityLoader;
import com.campfood.src.member.MemberService;
import com.campfood.src.member.entity.Member;
import com.campfood.src.review.dto.request.ReviewCreateDTO;
import com.campfood.src.review.dto.request.ReviewUpdateDTO;
import com.campfood.src.review.dto.response.ReviewInquiryByMemberDTO;
import com.campfood.src.review.dto.response.ReviewInquiryByStoreDTO;
import com.campfood.src.review.dto.response.ReviewPageResponse;
import com.campfood.src.review.entity.Review;
import com.campfood.src.review.entity.ReviewHeart;
import com.campfood.src.review.entity.ReviewImage;
import com.campfood.src.review.mapper.ReviewMapper;
import com.campfood.src.review.repository.ReviewHeartRepository;
import com.campfood.src.review.repository.ReviewImageRepository;
import com.campfood.src.review.repository.ReviewRepository;
import com.campfood.src.store.entity.Store;
import com.campfood.src.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService implements EntityLoader<Review, Long> {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewHeartRepository reviewHeartRepository;
    private final ReviewMapper reviewMapper;

    private final StoreService storeService;
    private final MemberService memberService;

    // 리뷰 생성 함수
    @Transactional
    public Long createReview(final Long storeId, ReviewCreateDTO request, List<MultipartFile> reviewImages) {

        // 가게 찾기
        Store store = storeService.loadEntity(storeId);

        Review savedReview = reviewRepository.save(reviewMapper.toReview(store, request));

        // 리뷰 이미지 저장
        if (reviewImages != null) {
            saveReviewImages(savedReview, uploadReviewImages(reviewImages));
        }

        // TODO: 멤버 averageRate 업데이트

        return savedReview.getId();
    }

    // 리뷰 수정 함수
    @Transactional
    public Long updateReview(final Long reviewId, ReviewUpdateDTO request, List<MultipartFile> reviewImages) {

        // 로그인 유저
        Member member = null;

        // 리뷰 찾기
        Review review = loadEntity(reviewId);

        if (review.getMember().getId().equals(member.getId())) {
            throw new RestApiException(ErrorCode.UNAUTHORIZED_REVIEW);
        }

        // 리뷰 정보 업데이트
        review.updateReview(request);

        // 기존 리뷰 이미지 삭제
        List<ReviewImage> oldReviewImages = reviewImageRepository.findAllByReview(review);
        //TODO: S3 사진 삭제 구현 후 삭제 개발

        // 새로운 리뷰 이미지 저장
        if (reviewImages != null) {
            saveReviewImages(review, uploadReviewImages(reviewImages));
        }

        // TODO: 멤버 averageRate 업데이트


        return review.getId();
    }

    // 리뷰 삭제 함수
    @Transactional
    public Long deleteReview(final Long reviewId) {

        // 로그인 유저
        Member member = null;

        // 리뷰 찾기
        Review review = loadEntity(reviewId);

        if (review.getMember().getId().equals(member.getId())) {
            throw new RestApiException(ErrorCode.UNAUTHORIZED_REVIEW);
        }

        // 리뷰 삭제
        review.delete();

        // TODO: 멤버 averageRate 업데이트

        return review.getId();
    }

    // 리뷰 좋아요 활성화/비활성화 함수
    @Transactional
    public boolean toggleReviewHeart(final Long reviewId) {

        // 로그인 유저
        Member member = null;

        // 리뷰 찾기
        Review review = loadEntity(reviewId);

        ReviewHeart reviewHeart = reviewHeartRepository.findByMemberAndReview(member, review)
                .orElse(reviewHeartRepository.save(reviewMapper.toReviewHeart(review, member)));

        reviewHeart.toggleStoreHeart();

        return reviewHeart.isChecked();
    }

    // 특정 가게 리뷰 조회 함수
    public ReviewPageResponse<ReviewInquiryByStoreDTO> inquiryReviewsByStore(Long storeId, Pageable pageable) {

        Store store = storeService.loadEntity(storeId);

        Page<Review> reviewPage = reviewRepository.findAllByStore(store, pageable);

        List<ReviewInquiryByStoreDTO> reviews = reviewPage.stream()
                .map(review ->
                        reviewMapper.toReviewInquiryByStoreDTO(
                                review,
                                calculateAverageRate(review),
                                reviewImageRepository.findAllByReview(review).stream()
                                        .map(ReviewImage::getUrl)
                                        .collect(Collectors.toList()),
                                getWriterInfo(review)
                        ))
                .collect(Collectors.toList());

        return new ReviewPageResponse<>(reviews, reviewPage.hasNext());
    }

    // 특정 멤버 리뷰 조회 함수
    public ReviewPageResponse<ReviewInquiryByMemberDTO> inquiryReviewsByMember(Long memberId, Pageable pageable) {

        // 로그인 유저
        Member member = null;

        // 타인 조회 시
        if (memberId != null)
            member = memberService.loadEntity(memberId);

        Page<Review> reviewPage = reviewRepository.findAllByMember(member, pageable);

        List<ReviewInquiryByMemberDTO> reviews = reviewPage.stream()
                .map(review ->
                        reviewMapper.toReviewInquiryByMemberDTO(
                                review,
                                calculateAverageRate(review),
                                reviewImageRepository.findAllByReview(review).stream()
                                        .map(ReviewImage::getUrl)
                                        .collect(Collectors.toList()),
                                reviewMapper.toStoreInfo(review.getStore())
                        ))
                .collect(Collectors.toList());

        return new ReviewPageResponse<>(reviews, reviewPage.hasNext());
    }

    // reviewImage 저장 함수
    private void saveReviewImages(Review review, List<String> imageUrls) {
        List<ReviewImage> reviewImages = imageUrls.stream()
                .map(imageUrl -> reviewMapper.toReviewImage(review, imageUrl))
                .collect(Collectors.toList());
        reviewImageRepository.saveAll(reviewImages);
    }

    // s3 업로드 함수
    private List<String> uploadReviewImages(List<MultipartFile> reviewImages) {
        //TODO: s3 사진 업로드 구현 후 개발
        return List.of();
    }

    // 리뷰 평균 평점 계산 함수
    private double calculateAverageRate(Review review) {
        double sum = review.getClean_rate() + review.getService_rate()
                + review.getTaste_rate() + review.getCost_effectiveness_rate();
        double average = sum / 4;

        String formatted = String.format("%.1f", average);
        return Double.parseDouble(formatted);
    }

    // 리뷰 작성자 정보 조회 함수
    private ReviewInquiryByStoreDTO.WriterInfo getWriterInfo(Review review) {
        Member member = review.getMember();

        return reviewMapper.toWriterInfo(
                member,
                memberService.findProfileImage(member),
                reviewRepository.countAllByMember(member)
        );
    }

    @Override
    public Review loadEntity(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RestApiException(ErrorCode.REVIEW_NOT_EXIST));
    }
}
