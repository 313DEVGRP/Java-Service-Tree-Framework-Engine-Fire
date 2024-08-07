package com.arms.api.util.errors.codes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum 에러코드 {

    서버정보_오류("등록된 서버 정보가 아닙니다."),
    서버_URI정보_오류("서버 URI 정보가 없습니다."),
    서버_ID정보_오류("서버 사용자 아이디 정보가 없습니다."),
    서버_PW_또큰_API토큰정보_오류("서버 비밀번호나 토큰 정보가 없습니다."),
    서버유형_정보오류("서버 유형 정보가 없습니다."),
    서버정보_생성_오류("서버정보 저장에 실패하였습니다."),
    서버정보_조회_오류("서버정보 조회에 실패하였습니다."),
    서버인덱스_NULL_오류("서버정보 인덱스가 존재하지 않습니다."),
    지라이슈_인덱스_NULL_오류("지라이슈 인덱스가 존재하지 않습니다."),
    지라이슈_인덱스_백업오류("지라이슈 인덱스 백업에 실패하였습니다."),
    지라이슈_인덱스_삭제오류("지라이슈 인덱스 삭제에 실패하였습니다."),


    파라미터_서버_아이디_없음("파라미터 서버 아이디가 없습니다."),
    파라미터_NULL_오류("파라미터 정보가 비어있습니다."),
    검색정보_오류("조회 대상 정보가 없습니다."),     //이슈 및 프로젝트 아이디 및 키 조회시 오류

    요청한_데이터가_유효하지않음("호출 된 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않습니다"),
    요청본문_오류체크("요청 본문이 없거나 JSON 형식이 아닙니다."), //이슈 생성 및 수정 관련 오류

    API경로_오류("잘못된 주소로 요청하였습니다."),   // 경로 오류

    사용자_정보조회_실패("클라우드 사용자 정보 조회(보고자와 할당자)를 실패하였습니다."),

    프로젝트_조회_오류("프로젝트 정보 가져오기에 실패하였습니다. 조회 대상 정보 확인이 필요합니다."),

    이슈생성_오류("이슈 생성시 오류 발생하여 이슈 생성에 실패하였습니다."),
    이슈수정_오류("이슈 수정시 오류가 발생하였습니다. 수정 대상 정보 확인이 필요합니다."),
    이슈전환_오류("이슈 상태 변경 시 오류가 발생하였습니다."), // 이슈 상태 변경 (지라: status id -> transition 조회 및 transition id로 상태 변경)

    이슈_조회_오류("이슈 정보 가져오기에 실패하였습니다. 조회 대상 정보 확인이 필요합니다."),
    이슈유형_조회_오류("이슈 유형 정보 가져오기에 실패하였습니다."),
    이슈상태_조회_오류("이슈 상태 정보 가져오기에 실패하였습니다."),
    이슈해결책_조회_오류("이슈 해결책 정보 가져오기에 실패하였습니다."),
    이슈우선순위_조회_오류("우선순위 정보 가져오기에 실패하였습니다."),
    워크로그_조회_오류("워크로그 정보 가져오기에 실패하였습니다."),
    이슈전환_조회_오류("이슈 상태 변경을 위한 Transition 조회에 실패하였습니다."),

    계정정보_조회_오류("계정 정보 조회에 실패하였습니다. 토큰및 아이디 정보 확인이 필요합니다."),


    Document_NULL_오류("Document가 Null입니다.")
    ;

    private final String errorMsg;

    public String getErrorMsg(Object... arg) {
        return String.format(errorMsg, arg);
    }
}
