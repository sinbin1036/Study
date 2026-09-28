package payroll;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * "직원(Employee)" 한 명의 정보를 담는 클래스(엔티티, Entity)입니다.
 *
 * <p>
 * 엔티티란 "데이터베이스 표(table)의 한 줄(행)과 1:1로 짝지어지는 자바 객체"입니다.
 * 엑셀 시트에 비유하면, 이 클래스는 "직원 명단 시트의 열(칸) 구성"이고,
 * 이 클래스로 만든 객체 하나하나가 "직원 한 명이 적힌 한 줄"입니다.
 * </p>
 *
 * <p>
 * {@code @Entity}: JPA(자바 객체 ↔ 데이터베이스 표를 자동으로 연결해 주는 기술)에게
 * "이 클래스를 데이터베이스 표로 저장해 줘"라고 알리는 표시입니다.
 * 표 이름은 따로 지정하지 않았으므로 클래스 이름(Employee)을 바탕으로 자동으로 정해집니다.
 * </p>
 */
@Entity
class Employee {

	// 직원의 고유 번호(주민번호나 사번처럼 겹치지 않는 값)
	// @Id: 이 필드가 표의 "기본 키(Primary Key, 각 줄을 구별하는 고유 번호)"라는 뜻입니다.
	// @GeneratedValue: 저장할 때 번호를 직접 넣지 않아도 데이터베이스가 1, 2, 3... 처럼 자동으로 매겨 줍니다.
	private @Id @GeneratedValue Long id;
	// 이름(성을 뺀 이름 부분, 예: "Bilbo")
	private String firstName;
	// 성(예: "Baggins")
	private String lastName;
	// 직무/역할(예: "burglar")
	private String role;

	/**
	 * 매개변수가 없는 기본 생성자입니다.
	 * JPA가 데이터베이스에서 값을 읽어 와 객체를 만들 때 이 생성자가 반드시 필요합니다.
	 * (먼저 빈 객체를 만든 뒤 값을 채워 넣는 방식이기 때문입니다.)
	 */
	Employee() {}

	/**
	 * 이름, 성, 역할을 받아 새 직원 객체를 만드는 생성자입니다.
	 * id는 데이터베이스에 저장될 때 자동으로 매겨지므로 여기서 받지 않습니다.
	 *
	 * @param firstName 이름
	 * @param lastName 성
	 * @param role 역할
	 */
	Employee(String firstName, String lastName, String role) {

		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	/**
	 * 이름과 성을 공백으로 이어 붙인 "전체 이름"을 돌려줍니다. (예: "Bilbo Baggins")
	 *
	 * <p>
	 * 실제 필드로 저장된 값은 아니지만, getter(값을 꺼내는 메서드)가 있으므로
	 * JSON(데이터를 주고받을 때 쓰는 "이름: 값" 형태의 텍스트 형식)으로 변환될 때
	 * "name"이라는 항목으로 함께 포함됩니다.
	 * </p>
	 *
	 * @return "이름 성" 형태의 문자열
	 */
	public String getName() {
		return this.firstName + " " + this.lastName;
	}

	/**
	 * "이름 성" 형태의 전체 이름을 받아서 공백을 기준으로 나누어 firstName과 lastName에 각각 저장합니다.
	 * 예: "Samwise Gamgee" → firstName="Samwise", lastName="Gamgee"
	 *
	 * <p>
	 * 주의: 공백이 없는 이름(예: "Gandalf")이 들어오면 parts[1]이 없어서 오류가 납니다.
	 * (코드에 그런 경우를 따로 처리하는 부분은 없습니다.)
	 * </p>
	 *
	 * @param name 공백으로 구분된 전체 이름
	 */
	public void setName(String name) {

		// split(" "): 문자열을 공백 기준으로 잘라 배열(여러 값을 순서대로 담는 상자)로 만듭니다.
		String[] parts = name.split(" ");
		this.firstName = parts[0];
		this.lastName = parts[1];
	}

	// ----- 아래는 getter(값 꺼내기) / setter(값 바꾸기) 메서드들입니다. -----
	// 필드를 private(외부에서 직접 접근 불가)로 숨기고, 이 메서드들을 통해서만 읽고 쓰도록 합니다.
	// JSON 변환 도구(Jackson)와 JPA도 이 메서드들을 이용해 값을 읽고 씁니다.

	/** 직원 고유 번호(id)를 돌려줍니다. */
	public Long getId() {
		return this.id;
	}

	/** 이름(firstName)을 돌려줍니다. */
	public String getFirstName() {
		return this.firstName;
	}

	/** 성(lastName)을 돌려줍니다. */
	public String getLastName() {
		return this.lastName;
	}

	/** 역할(role)을 돌려줍니다. */
	public String getRole() {
		return this.role;
	}

	/** 직원 고유 번호(id)를 바꿉니다. */
	public void setId(Long id) {
		this.id = id;
	}

	/** 이름(firstName)을 바꿉니다. */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/** 성(lastName)을 바꿉니다. */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/** 역할(role)을 바꿉니다. */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * 두 직원 객체가 "내용상 같은 직원인지" 비교합니다.
	 *
	 * <p>
	 * 자바에서 기본 비교는 "메모리상 같은 물건인지"만 봅니다.
	 * 이 메서드를 재정의(@Override: 부모에게 물려받은 기능을 내 방식으로 다시 만듦)해서
	 * id, 이름, 성, 역할이 모두 같으면 같은 직원으로 판단하도록 바꿉니다.
	 * </p>
	 *
	 * @param o 비교할 다른 객체
	 * @return 모든 값이 같으면 true, 아니면 false
	 */
	@Override
	public boolean equals(Object o) {

		// 완전히 같은 객체(같은 메모리 주소)라면 당연히 같습니다.
		if (this == o)
			return true;
		// 비교 대상이 Employee가 아니면(다른 종류의 객체면) 다릅니다.
		if (!(o instanceof Employee))
			return false;
		// Employee 타입으로 형변환한 뒤 각 필드를 하나씩 비교합니다.
		// Objects.equals는 값이 null(비어 있음)이어도 오류 없이 안전하게 비교해 줍니다.
		Employee employee = (Employee) o;
		return Objects.equals(this.id, employee.id) && Objects.equals(this.firstName, employee.firstName)
				&& Objects.equals(this.lastName, employee.lastName) && Objects.equals(this.role, employee.role);
	}

	/**
	 * 객체의 "해시 코드(요약 번호)"를 계산합니다.
	 *
	 * <p>
	 * 해시 코드는 HashMap/HashSet 같은 자료구조가 객체를 빠르게 찾기 위해 쓰는 번호입니다.
	 * equals가 같다고 판단한 두 객체는 반드시 같은 해시 코드를 가져야 하므로,
	 * equals에서 비교한 것과 같은 필드들로 계산합니다.
	 * </p>
	 *
	 * @return 필드 값들로 계산한 정수
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.firstName, this.lastName, this.role);
	}

	/**
	 * 객체를 사람이 읽기 쉬운 문자열로 바꿔 줍니다. 로그(실행 기록)를 찍을 때 주로 쓰입니다.
	 * 예: Employee{id=1, firstName='Bilbo', lastName='Baggins', role='burglar'}
	 *
	 * @return 직원 정보를 담은 문자열
	 */
	@Override
	public String toString() {
		return "Employee{" + "id=" + this.id + ", firstName='" + this.firstName + '\'' + ", lastName='" + this.lastName
				+ '\'' + ", role='" + this.role + '\'' + '}';
	}
}
