package payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * "직원을 찾을 수 없음" 오류가 났을 때 사용자에게 보낼 응답을 정해 주는 클래스입니다.
 *
 * <p>비유하자면 고객센터의 "문제 해결 담당자"입니다. 컨트롤러에서 EmployeeNotFoundException(직원 없음 예외)이
 * 발생하면 프로그램이 그냥 멈추거나 알아보기 힘든 오류 화면을 보여 주는 대신, 이 담당자가 가로채서
 * "404 Not Found + 'Could not find employee 번호'"라는 깔끔한 응답으로 바꿔 줍니다.</p>
 *
 * <p>(예외: 프로그램 실행 중 "정상적으로 진행할 수 없는 상황"을 알리는 신호)</p>
 */
// @RestControllerAdvice: 모든 컨트롤러에 공통으로 적용되는 "조언자(Advice)"라는 표시입니다.
//                       주로 여러 컨트롤러에서 발생하는 예외를 한 곳에서 처리할 때 씁니다.
//                       (@ControllerAdvice + @ResponseBody 를 합친 것으로, 돌려주는 값을 응답 본문에 그대로 담습니다)
@RestControllerAdvice
class EmployeeNotFoundAdvice {

	/**
	 * EmployeeNotFoundException이 발생했을 때 실행되는 처리 메서드입니다.
	 *
	 * <p>@ExceptionHandler(EmployeeNotFoundException.class): "이 종류의 예외가 발생하면 이 메서드를 실행하라"는 표시.<br>
	 * {@code @ResponseStatus(HttpStatus.NOT_FOUND)}: 응답의 HTTP 상태 코드를 404 Not Found(요청한 것을 찾을 수 없음)로 정합니다.</p>
	 *
	 * @param ex 발생한 예외 객체
	 * @return 응답 본문에 담길 오류 메시지 (예: "Could not find employee 99")
	 */
	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String employeeNotFoundHandler(EmployeeNotFoundException ex) {
		// 예외를 만들 때 넣어 둔 메시지를 그대로 꺼내 돌려줍니다.
		return ex.getMessage();
	}
}
