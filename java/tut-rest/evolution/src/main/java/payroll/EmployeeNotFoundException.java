package payroll;

/**
 * 요청한 번호(id)의 직원이 데이터베이스에 없을 때 발생시키는 "사용자 정의 예외" 클래스입니다.
 *
 * <p>예외(Exception)란? 프로그램이 정상적으로 진행할 수 없는 상황을 알리는 "경보"입니다.
 * 예를 들어 "5번 직원 보여 줘"라는 요청이 왔는데 5번 직원이 없다면 이 경보를 울립니다.
 * 울린 경보는 EmployeeNotFoundAdvice가 받아서 404(찾을 수 없음) 응답으로 바꿔 줍니다.</p>
 *
 * <p>RuntimeException을 상속(extends: 부모의 기능을 물려받음)했기 때문에,
 * 이 예외를 쓰는 메서드마다 "이 예외가 날 수 있음"을 일일이 선언(throws)하지 않아도 됩니다.</p>
 */
class EmployeeNotFoundException extends RuntimeException {

	/**
	 * 찾지 못한 직원 번호를 받아 예외를 만듭니다.
	 *
	 * @param id 찾지 못한 직원 번호
	 */
	EmployeeNotFoundException(Long id) {
		// super(...): 부모 클래스(RuntimeException)의 생성자를 호출해 오류 메시지를 저장합니다.
		// 예) id가 99라면 메시지는 "Could not find employee 99"
		super("Could not find employee " + id);
	}
}
