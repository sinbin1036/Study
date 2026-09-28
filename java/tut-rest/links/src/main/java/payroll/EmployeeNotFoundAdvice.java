package payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 직원을 찾지 못했을 때 발생하는 예외({@link EmployeeNotFoundException})를 붙잡아서
 * 사용자에게 보기 좋은 오류 응답으로 바꿔 주는 "고객 응대 담당" 클래스입니다.
 *
 * <p>
 * {@code @RestControllerAdvice}: 모든 컨트롤러(요청을 받는 창구)에 공통으로 적용되는
 * "조언자(Advice)"라는 표시입니다. 어느 컨트롤러에서 예외가 터지든 여기서 한꺼번에 처리할 수 있습니다.
 * 또 이름에 "Rest"가 붙어 있어서, 메서드가 돌려준 값을 화면(HTML)이 아니라
 * 응답 본문(body)에 그대로 담아 보냅니다.
 * </p>
 */
@RestControllerAdvice
class EmployeeNotFoundAdvice {

	/**
	 * EmployeeNotFoundException이 발생했을 때 실행되는 처리 메서드입니다.
	 *
	 * <ul>
	 * <li>{@code @ExceptionHandler(EmployeeNotFoundException.class)}: "이 종류의 예외가 발생하면 이 메서드를 실행해라"라는 뜻입니다.</li>
	 * <li>{@code @ResponseStatus(HttpStatus.NOT_FOUND)}: 응답의 HTTP 상태 코드를 404(Not Found: 요청한 대상을 찾을 수 없음)로 정합니다.
	 * (HTTP 상태 코드: 서버가 요청 결과를 숫자로 알려 주는 것. 예: 200=성공, 201=새로 만들어짐, 404=없음, 405=허용되지 않은 동작)</li>
	 * </ul>
	 *
	 * @param ex 발생한 예외 객체 (안에 "Could not find employee N" 메시지가 들어 있음)
	 * @return 예외 메시지 문자열 → 그대로 응답 본문이 됩니다.
	 */
	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String employeeNotFoundHandler(EmployeeNotFoundException ex) {
		return ex.getMessage();
	}
}
