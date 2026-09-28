package payroll;

/**
 * 요청한 번호(id)의 주문을 찾을 수 없을 때 "던지는(발생시키는)" 예외(Exception) 클래스입니다.
 *
 * <p>
 * 예외란 프로그램 실행 중에 "정상 흐름으로는 더 진행할 수 없는 문제"가 생겼다고 알리는 신호입니다.
 * 식당에서 "없는 메뉴 번호"를 주문하면 직원이 "그 메뉴는 없습니다"라고 알려 주는 것과 비슷합니다.
 * </p>
 *
 * <p>
 * {@code RuntimeException}을 상속했기 때문에, 이 예외를 쓰는 메서드가
 * "이 예외가 날 수 있음"을 따로 선언(throws)하지 않아도 됩니다.
 * </p>
 *
 * <p>
 * 참고: 이 모듈에는 직원용 {@link EmployeeNotFoundAdvice}(예외 → 404 응답 변환기)만 있고,
 * 주문용 Advice 클래스는 없습니다. 따라서 이 예외는 스프링의 기본 오류 처리 방식으로 응답됩니다.
 * </p>
 */
class OrderNotFoundException extends RuntimeException {

	/**
	 * 예외를 만들 때 "Could not find order 번호"(주문 번호 N을 찾을 수 없음)라는 메시지를 함께 담습니다.
	 *
	 * @param id 찾지 못한 주문의 번호
	 */
	OrderNotFoundException(Long id) {
		// super(...): 부모 클래스(RuntimeException)의 생성자를 호출해 오류 메시지를 저장합니다.
		super("Could not find order " + id);
	}
}
