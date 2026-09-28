package payroll;

/**
 * 요청한 직원을 찾을 수 없을 때 "문제가 생겼다"고 알리기 위해 던지는 예외(Exception) 클래스입니다.
 *
 * <p>예외란 프로그램 실행 중 정상 흐름으로 처리할 수 없는 상황이 생겼을 때 발생시키는 "경보"입니다.
 * 예를 들어 99번 직원을 조회했는데 데이터베이스에 없다면, 이 예외를 던져서(throw)
 * 지금 하던 처리를 중단하고 문제 상황을 알립니다.</p>
 *
 * <p>{@code RuntimeException}을 상속했기 때문에, 이 예외를 사용하는 쪽에서
 * 반드시 try-catch로 감싸야 하는 의무가 없습니다(언체크 예외).
 * 대신 {@link EmployeeNotFoundAdvice}가 이 예외를 받아서 사용자에게
 * "404 Not Found(찾을 수 없음)" 응답으로 바꿔 돌려줍니다.</p>
 */
class EmployeeNotFoundException extends RuntimeException {

	/**
	 * 찾지 못한 직원의 id를 받아 예외를 만듭니다.
	 *
	 * <p>{@code super(...)}는 부모 클래스({@code RuntimeException})의 생성자를 호출하는 것으로,
	 * 여기서는 "Could not find employee 번호"(번호에 해당하는 직원을 찾을 수 없음)라는
	 * 오류 메시지를 예외 안에 담아 둡니다. 이 메시지는 나중에 {@code getMessage()}로 꺼낼 수 있습니다.</p>
	 *
	 * @param id 찾으려 했지만 존재하지 않았던 직원의 고유 번호
	 */
	EmployeeNotFoundException(Long id) {
		super("Could not find employee " + id);
	}
}
