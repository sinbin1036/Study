package payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 직원을 찾지 못했을 때 발생하는 예외({@link EmployeeNotFoundException})를 가로채서
 * 사용자에게 알기 쉬운 응답으로 바꿔 주는 "오류 안내 창구" 클래스입니다.
 * <p>
 * {@code @RestControllerAdvice}는 "모든 컨트롤러에 공통으로 적용되는 조언자(Advice)"라는 뜻입니다.
 * 어느 컨트롤러에서든 예외가 발생하면 이 클래스가 대신 나서서 처리해 줍니다.
 * 비유하면, 매장 어디서 문제가 생기든 달려와서 손님에게 상황을 설명해 주는 고객센터 직원입니다.
 * 또한 이름에 "Rest"가 붙어 있어서, 메서드가 돌려주는 값을 화면(HTML 페이지)이 아니라
 * 응답 본문(body)에 데이터 그대로 담아서 보냅니다.
 * <p>
 * 이 클래스가 없다면 예외 발생 시 스프링의 기본 오류 응답(보통 500 서버 오류)이 나가겠지만,
 * 이 클래스 덕분에 "404 Not Found + 'Could not find employee 99'" 같은 명확한 응답이 나갑니다.
 */
@RestControllerAdvice
class EmployeeNotFoundAdvice {

	/**
	 * EmployeeNotFoundException이 발생했을 때 실행되는 처리 메서드입니다.
	 * <ul>
	 *   <li>{@code @ExceptionHandler(EmployeeNotFoundException.class)} :
	 *       "이 종류의 예외가 발생하면 이 메서드를 실행하라"는 표시입니다.</li>
	 *   <li>{@code @ResponseStatus(HttpStatus.NOT_FOUND)} :
	 *       응답의 HTTP 상태 코드를 404(Not Found, "찾을 수 없음")로 정합니다.
	 *       (HTTP 상태 코드: 서버가 요청 결과를 숫자로 알려 주는 것. 200=성공, 404=없음, 500=서버 오류 등)</li>
	 * </ul>
	 *
	 * @param ex 발생한 예외 객체 (안에 오류 메시지가 들어 있음)
	 * @return 사용자에게 보여 줄 오류 메시지 문자열 (예: "Could not find employee 99")
	 */
	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String employeeNotFoundHandler(EmployeeNotFoundException ex) {
		// 예외에 담겨 있던 메시지를 꺼내서 응답 본문으로 그대로 돌려줍니다.
		return ex.getMessage();
	}
}
