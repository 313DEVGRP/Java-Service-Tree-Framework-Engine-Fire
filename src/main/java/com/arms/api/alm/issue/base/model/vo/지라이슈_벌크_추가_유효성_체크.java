package com.arms.api.alm.issue.base.model.vo;
import com.arms.api.util.errors.codes.에러코드;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class 지라이슈_벌크_추가_유효성_체크 implements ConstraintValidator<지라이슈_벌크_추가_요청_유효성, 지라이슈_벌크_추가_요청> {

    @Override
    public boolean isValid(지라이슈_벌크_추가_요청 지라이슈_벌크_추가_요청, ConstraintValidatorContext context) {
        if (지라이슈_벌크_추가_요청.get지라서버_아이디() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("지라이슈_벌크_추가하기 Error: 서버_아이디 " + 에러코드.파라미터_서버_아이디_없음.getErrorMsg())
                    .addConstraintViolation();
            return false;
        }

        if (지라이슈_벌크_추가_요청.get이슈_키() == null || 지라이슈_벌크_추가_요청.get이슈_키().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("지라이슈_벌크_추가하기 Error 이슈_키 " + 에러코드.파라미터_NULL_오류.getErrorMsg())
                    .addConstraintViolation();
            return false;
        }

        if (지라이슈_벌크_추가_요청.get프로젝트키_또는_아이디() == null
                || 지라이슈_벌크_추가_요청.get프로젝트키_또는_아이디().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("지라이슈_벌크_추가하기 Error 프로젝트키_또는_아이디 " + 에러코드.파라미터_NULL_오류.getErrorMsg())
                    .addConstraintViolation();
            return false;
        }

        if (지라이슈_벌크_추가_요청.get제품서비스_아이디() == null
                || 지라이슈_벌크_추가_요청.get제품서비스_버전들() == null
                || 지라이슈_벌크_추가_요청.get제품서비스_버전들().length==0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("지라이슈_벌크_추가하기 Error 제품서비스_아이디 또는 제품서비스_버전 " + 에러코드.파라미터_NULL_오류.getErrorMsg())
                    .addConstraintViolation();
            return false;
        }

        if (지라이슈_벌크_추가_요청.getCReqLink() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("지라이슈_벌크_추가하기 Error cReqLink " + 에러코드.파라미터_NULL_오류.getErrorMsg())
                    .addConstraintViolation();
            return false;
        }
        return  true;
    }
}
