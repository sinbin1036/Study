package payroll;

/**
 * "요청한 번호의 직원을 찾을 수 없다"는 상황을 알리기 위한 예외(Exception) 클래스입니다.
 * <p>
 * 예외(Exception)란 프로그램 실행 중에 생긴 "문제 상황"을 알리는 신호입니다.
 * 예를 들어 사용자가 존재하지 않는 99번 직원을 조회하면, 이 예외를 "던져서(throw)"
 * 정상 흐름을 멈추고 "문제가 생겼어요!"라고 알립니다.
 * <p>
 * {@code RuntimeException}을 상속했기 때문에, 이 예외를 쓰는 메서드가
 * "이 예외가 발생할 수 있음"을 미리 선언(throws)하지 않아도 됩니다.
 * <p>
 * 이렇게 던져진 예외는 {@link EmployeeNotFoundAdvice}가 가로채서
 * 사용자에게 "404 Not Found(찾을 수 없음)" 응답으로 바꿔 보내 줍니다.
 */
class EmployeeNotFoundException extends RuntimeException {

	/**
	 * 예외 객체를 만드는 생성자입니다.
	 *
	 * @param id 찾지 못한 직원의 번호
	 */
	EmployeeNotFoundException(Long id) {
		// 부모 클래스(RuntimeException)에 오류 메시지를 전달합니다.
		// 예: id가 99이면 메시지는 "Could not find employee 99"(99번 직원을 찾을 수 없음)가 됩니다.
		// 이 메시지는 나중에 getMessage()로 꺼내어 사용자에게 그대로 보여 줍니다.
		super("Could not find employee " + id);
	}
}
