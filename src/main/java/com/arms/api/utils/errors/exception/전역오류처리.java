package com.arms.api.utils.errors.exception;


import com.arms.api.utils.errors.codes.에러코드;
import com.arms.api.utils.response.응답처리;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller 내에서 발생하는 Exception 대해서 Catch 하여 응답값(Response)을 보내주는 기능을 수행함.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class 전역오류처리 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private ResponseEntity<응답처리.ApiResult<?>> newResponse(String message, HttpStatus status) {
        HttpHeaders headers = getHttpHeaders();
        return new ResponseEntity<>(응답처리.error(message, status), headers, status);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return headers;
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> onException(Exception e) {
        e.printStackTrace();
        System.out.println(e.getMessage());
        return newResponse(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
    /*
     * connectId 값이 정상적으로 넘어오지 않았을 때 오류 처리
     * */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<?> 커넥트아이디_오류체크(MissingPathVariableException 에러) {

        String variableName = 에러.getVariableName();
        로그.error("커넥트 아이디:", 에러);
        if ("connectId".equals(variableName)) {
            return newResponse(에러코드.파라미터_서버_아이디_없음.getErrorMsg(),HttpStatus.BAD_REQUEST);
        }
        return newResponse(에러코드.파라미터_서버_아이디_없음.getErrorMsg(),HttpStatus.BAD_REQUEST);
    }
    /**
     * API 호출 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우 @Valid 사용 (추후 필요 가능성)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<?> 전송데이터_유효성체크(MethodArgumentNotValidException 에러) {
        로그.error("호출 된 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않습니다 :", 에러);
        return newResponse(에러코드.요청한_데이터가_유효하지않음.getErrorMsg(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 잘못된 서버 요청일 경우 발생한 경우(바디에 데이터가 없거나 json 타입이 아닌경우)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<?> HttpMessageNotReadableException(HttpMessageNotReadableException 에러) {
        로그.error("요청 본문이 없거나 JSON 형식이 아닙니다. ", 에러);
        return newResponse(에러코드.요청본문_오류체크.getErrorMsg(), HttpStatus.BAD_REQUEST);
    }


    /**
     * 잘못된 주소로 요청 한 경우
     */
//    @ExceptionHandler(NoHandlerFoundException.class)
//    protected ResponseEntity<?> API요청경로_체크(NoHandlerFoundException 에러) {
//        로그.error("잘못된 주소로 요청하였습니다. ", 에러);
//        return newResponse(에러코드.API경로_오류.getErrorMsg(), HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException 에러) {
        로그.error(에러.getMessage());
        return newResponse(에러.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] NULL 값이 발생한 경우
     */
//    @ExceptionHandler(NullPointerException.class)
//    protected ResponseEntity<에러응답처리> handleNullPointerException(NullPointerException 에러) {
//        log.error("handleNullPointerException", 에러);
//        final 에러응답처리 응답_에러 = 에러응답처리.of(에러코드.NULL_POINT_ERROR, 에러.getMessage());
//        return new ResponseEntity<>(응답_에러, HttpStatus.);
//    }

//    @ExceptionHandler(서비스오류처리.class)
//    public ResponseEntity<에러응답처리> handleCustomException(서비스오류처리 오류) {
//
//        System.out.println("오류"+오류.getMessage()+"         "+오류.get에러코드());
//        final 에러응답처리 response = 에러응답처리.of(오류.get에러코드(), 오류.getMessage());
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
}
