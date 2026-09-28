package payroll;

/**
 * 요청한 번호(id)의 직원을 찾을 수 없을 때 "던지는(발생시키는)" 예외(Exception) 클래스입니다.
 *
 * <p>
 * 예외란 프로그램 실행 중에 "정상 흐름으로는 더 진행할 수 없는 문제"가 생겼다고 알리는 신호입니다.
 * 회사 안내 데스크에 "없는 사번"을 물어보면 "그런 직원은 없습니다"라고 알려 주는 것과 비슷합니다.
 * </p>
 *
 * <p>
 * {@code RuntimeException}을 상속했기 때문에, 이 예외를 쓰는 메서드가
 * "이 예외가 날 수 있음"을 따로 선언(throws)하지 않아도 됩니다.
 * 이 예외가 발생하면 {@link EmployeeNotFoundAdvice}가 가로채서
 * HTTP 404(Not Found: 요청한 대상이 없음) 응답으로 바꿔 줍니다.
 * </p>
 */
class EmployeeNotFoundException extends RuntimeException {

	/**
	 * 예외를 만들 때 "Could not find employee 번호"(직원 번호 N을 찾을 수 없음)라는 메시지를 함께 담습니다.
	 *
	 * @param id 찾지 못한 직원의 번호
	 */
	EmployeeNotFoundException(Long id) {
		// super(...): 부모 클래스(RuntimeException)의 생성자를 호출해 오류 메시지를 저장합니다.
		super("Could not find employee " + id);
	}
}
