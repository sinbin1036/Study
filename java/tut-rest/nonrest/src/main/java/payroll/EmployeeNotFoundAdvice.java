package payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 직원을 찾지 못했을 때 발생한 예외를 받아서, 사용자에게 보기 좋은 오류 응답으로 바꿔 주는 클래스입니다.
 *
 * <p>{@code @RestControllerAdvice}는 "모든 컨트롤러를 옆에서 도와주는 조언자"라는 뜻입니다.
 * 컨트롤러(요청을 받아 처리하는 클래스)에서 예외가 터지면, 스프링이 이 클래스로 넘겨서
 * 처리하게 합니다. 회사로 치면 각 부서에서 생긴 민원을 한곳에서 모아 처리하는 "고객 응대 창구"와 같습니다.
 * 또한 여기서 반환하는 값은 화면(HTML 페이지)이 아니라 응답 본문(body)에 그대로 담겨 전달됩니다.</p>
 */
@RestControllerAdvice
class EmployeeNotFoundAdvice {

	/**
	 * {@link EmployeeNotFoundException}이 발생했을 때 자동으로 호출되는 메서드입니다.
	 *
	 * <p>처리 순서:</p>
	 * <ol>
	 *   <li>컨트롤러에서 {@code EmployeeNotFoundException}이 던져집니다.</li>
	 *   <li>{@code @ExceptionHandler(EmployeeNotFoundException.class)} 표식을 보고
	 *       스프링이 "이 예외는 이 메서드가 담당"이라고 판단해 이 메서드를 호출합니다.</li>
	 *   <li>{@code @ResponseStatus(HttpStatus.NOT_FOUND)} 때문에 응답의 상태 코드가
	 *       404(Not Found: 요청한 대상을 찾을 수 없음)로 설정됩니다.
	 *       상태 코드는 서버가 요청 결과를 숫자로 알려 주는 약속으로, 200은 성공, 404는 없음, 500은 서버 오류 등을 뜻합니다.</li>
	 *   <li>예외에 담긴 메시지(예: "Could not find employee 99")를 문자열로 반환하면,
	 *       그 글자가 그대로 응답 본문으로 사용자에게 전달됩니다.</li>
	 * </ol>
	 *
	 * @param ex 발생한 "직원 없음" 예외 객체
	 * @return 사용자에게 보여 줄 오류 메시지 문자열
	 */
	@ExceptionHandler(EmployeeNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String employeeNotFoundHandler(EmployeeNotFoundException ex) {
		return ex.getMessage();
	}
}
