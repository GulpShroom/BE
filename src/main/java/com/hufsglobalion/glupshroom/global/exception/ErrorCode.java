package com.hufsglobalion.glupshroom.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_PRODUCT_SCAN_REQUEST(HttpStatus.BAD_REQUEST, "E400", "시리얼 넘버 또는 QR 코드가 필요합니다"),
    PRODUCT_MASTER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "제품 정보를 찾을 수 없습니다"),
    PRODUCT_SCAN_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "정품 인증 중 오류가 발생했습니다"),
    GENERATION_LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "해당 세대의 편지를 찾을 수 없습니다"),

    UNSUPPORTED_PROFILE_TYPE(HttpStatus.BAD_REQUEST, "E400", "지원하지 않는 프로필 타입입니다"),
    PROFILE_SELECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "프로필 선택 중 오류가 발생했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "서버 내부 오류가 발생했습니다"),

    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E400", "필수 값이 누락되었습니다"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "제품을 찾을 수 없습니다"),
    PRODUCT_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "E403", "해당 제품의 현재 소유자만 여정을 등록할 수 있습니다"),
    JOURNEY_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "여정을 찾을 수 없습니다"),

    INVALID_PRODUCT_LIST_STATUS(HttpStatus.BAD_REQUEST, "E400", "소유 상태 조회 조건이 올바르지 않습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "사용자를 찾을 수 없습니다"),
    PRODUCT_LIST_RETRIEVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "내 제품 리스트 조회 중 오류가 발생했습니다"),

    CANNOT_BUY_OWN_PRODUCT(HttpStatus.BAD_REQUEST, "E400", "본인의 제품은 구매할 수 없습니다"),
    RESELL_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "리셀글을 찾을 수 없습니다"),
    TRANSFER_ALREADY_IN_PROGRESS(HttpStatus.CONFLICT, "E409", "이미 진행 중인 이전이 있습니다"),
    TRANSFER_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "계승 시작 중 오류가 발생했습니다"),

    LETTER_CONTENT_REQUIRED(HttpStatus.BAD_REQUEST, "E400", "편지 내용을 입력해주세요"),
    LETTER_WRITE_FORBIDDEN(HttpStatus.FORBIDDEN, "E403", "편지 작성 권한이 없습니다"),
    TRANSFER_NOT_FOUND(HttpStatus.NOT_FOUND, "E404", "이전 요청을 찾을 수 없습니다"),
    LETTER_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "편지 저장 중 오류가 발생했습니다"),
    LETTER_DRAFT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E500", "초안 생성 중 오류가 발생했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
