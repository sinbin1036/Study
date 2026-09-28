package payroll;

// Objects: 두 값이 같은지 비교(equals)하거나 해시값(hash)을 만들 때 null 걱정 없이 쓸 수 있게 도와주는 자바 기본 도구 모음
import java.util.Objects;

// jakarta.persistence: JPA(자바 객체를 데이터베이스 표(테이블)에 자동으로 저장/조회해 주는 표준 기술)에서 쓰는 표시(어노테이션)들
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * 직원(Employee) 한 명의 정보를 담는 "엔티티(Entity)" 클래스입니다.
 *
 * <p>엔티티란? 데이터베이스의 표(테이블) 한 줄(행)과 1:1로 대응되는 자바 객체를 말합니다.
 * 엑셀 표에 비유하면, 이 클래스는 "직원 명단" 시트의 열 제목(아이디, 이름, 성, 역할)을 정의하고,
 * 이 클래스로 만든 객체 하나하나가 명단의 한 줄(직원 한 명)이 됩니다.</p>
 *
 * <p><b>이 모듈(evolution)의 핵심 - API 진화와 하위 호환성</b><br>
 * 예전 버전에서는 이름을 {@code name} 하나(예: "Bilbo Baggins")로 저장했습니다.
 * 이번 버전에서는 이를 {@code firstName}(이름, 예: "Bilbo")과 {@code lastName}(성, 예: "Baggins")으로 나누었습니다.
 * 하지만 이미 예전 방식({@code name})으로 데이터를 주고받던 프로그램(클라이언트)들이 있을 수 있습니다.
 * 그들이 갑자기 고장 나지 않도록, {@link #getName()}과 {@link #setName(String)}을 남겨 두어
 * 예전 방식의 {@code name}도 계속 쓸 수 있게 했습니다.
 * 이처럼 "새 버전이 나와도 옛날 사용자가 그대로 쓸 수 있게 해 주는 성질"을 하위 호환성이라고 합니다.
 * (비유: 새 콘센트 규격을 도입하면서도 예전 플러그를 꽂을 수 있게 어댑터를 함께 제공하는 것)</p>
 *
 * <p>결과적으로 JSON(프로그램끼리 데이터를 주고받는 텍스트 형식)으로 변환될 때
 * {@code id, firstName, lastName, role}뿐 아니라 getName() 덕분에 {@code name}도 함께 포함됩니다.</p>
 */
// @Entity: "이 클래스는 데이터베이스 테이블과 연결되는 엔티티입니다"라고 JPA에게 알려 주는 표시.
//          이 표시가 있으면 JPA가 알아서 Employee 테이블을 만들고 저장/조회를 처리해 줍니다.
@Entity
class Employee {

	// id: 직원마다 붙는 고유 번호(주민등록번호처럼 절대 겹치지 않는 값)
	// @Id: 이 필드가 테이블의 "기본 키"(각 행을 구분하는 고유 식별자)임을 뜻합니다.
	// @GeneratedValue: 번호를 우리가 직접 정하지 않고, 저장할 때 데이터베이스/JPA가 1, 2, 3... 처럼 자동으로 매겨 줍니다.
	private @Id @GeneratedValue Long id;
	// firstName: 이름(예: "Bilbo") - 예전의 name 필드를 쪼개서 새로 생긴 필드
	private String firstName;
	// lastName: 성(예: "Baggins") - 예전의 name 필드를 쪼개서 새로 생긴 필드
	private String lastName;
	// role: 직무/역할(예: "burglar" 좀도둑, "thief" 도둑 - 예제용 재미있는 값)
	private String role;

	/**
	 * 아무 값도 받지 않는 기본 생성자입니다.
	 * JPA가 데이터베이스에서 데이터를 읽어 와 객체를 만들 때 이 "빈 껍데기" 생성자가 필요합니다.
	 * (먼저 빈 객체를 만든 뒤 값을 하나씩 채워 넣는 방식이기 때문)
	 */
	Employee() {}

	/**
	 * 이름, 성, 역할을 받아서 새 직원 객체를 만드는 생성자입니다.
	 * id는 넣지 않습니다. 저장할 때 자동으로 부여되기 때문입니다(@GeneratedValue).
	 *
	 * @param firstName 이름
	 * @param lastName  성
	 * @param role      역할
	 */
	Employee(String firstName, String lastName, String role) {

		// this.필드 = 매개변수 : 전달받은 값을 이 객체 자신의 필드에 저장합니다.
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	/**
	 * [하위 호환용] 예전 방식의 "전체 이름"을 돌려줍니다.
	 * 실제로 name이라는 필드는 없지만, 이름과 성을 공백 한 칸으로 이어 붙여 만들어 냅니다.
	 * 예) firstName="Bilbo", lastName="Baggins" → "Bilbo Baggins"
	 *
	 * <p>JSON으로 변환될 때 getXxx() 형태의 메서드는 "xxx"라는 항목으로 들어가므로,
	 * 이 메서드 덕분에 응답 JSON에 예전처럼 "name" 항목이 계속 나타납니다.</p>
	 *
	 * @return "이름 성" 형태의 전체 이름
	 */
	public String getName() {
		return this.firstName + " " + this.lastName;
	}

	/**
	 * [하위 호환용] 예전 방식의 "전체 이름" 하나를 받아서 이름과 성으로 나누어 저장합니다.
	 * 예) "Frodo Baggins" → firstName="Frodo", lastName="Baggins"
	 *
	 * <p>예전 클라이언트가 {"name": "Frodo Baggins"} 처럼 보내도 새 구조(firstName/lastName)에 맞게 저장됩니다.</p>
	 *
	 * <p>주의: 공백(" ") 기준으로 나누어 첫 번째 조각을 이름, 두 번째 조각을 성으로 씁니다.
	 * 공백이 없는 이름(예: "Bilbo")이 들어오면 두 번째 조각(parts[1])이 없어서 오류가 발생하고,
	 * 공백이 여러 개면 세 번째 이후 조각은 버려집니다. (예제를 단순하게 하기 위한 구현입니다)</p>
	 *
	 * @param name "이름 성" 형태의 전체 이름
	 */
	public void setName(String name) {
		// split(" "): 문자열을 공백 기준으로 잘라 배열(여러 값을 순서대로 담는 상자)로 만듭니다.
		String[] parts = name.split(" ");
		// 배열의 0번째(첫 번째) 조각 → 이름
		this.firstName = parts[0];
		// 배열의 1번째(두 번째) 조각 → 성
		this.lastName = parts[1];
	}

	// ----- 아래는 게터(getter)와 세터(setter)입니다. -----
	// 게터: 필드 값을 "읽어 가는" 메서드, 세터: 필드 값을 "바꾸는" 메서드.
	// 필드는 private(외부에서 직접 접근 불가)이므로, 이 메서드들을 통해서만 값을 읽고 쓸 수 있습니다.
	// JSON 변환 도구와 JPA도 이 메서드들을 이용해 값을 읽고 채웁니다.

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
	 * 두 직원 객체가 "내용상 같은지" 비교합니다.
	 * 자바의 기본 비교는 "메모리상 같은 객체인가"만 보지만, 여기서는
	 * id, 이름, 성, 역할이 모두 같으면 같은 직원으로 판단하도록 규칙을 새로 정합니다.
	 *
	 * {@code @Override}: 부모 클래스(Object)에 이미 있는 메서드를 "내 방식으로 다시 정의한다"는 표시입니다.
	 *
	 * @param o 비교할 대상 객체
	 * @return 같으면 true, 다르면 false
	 */
	@Override
	public boolean equals(Object o) {

		// 1단계: 완전히 같은 객체(자기 자신)라면 당연히 같음
		if (this == o)
			return true;
		// 2단계: 비교 대상이 Employee가 아니면(또는 null이면) 다름
		if (!(o instanceof Employee))
			return false;
		// 3단계: Employee 타입으로 바꿔서(형변환) 각 필드를 하나씩 비교
		Employee employee = (Employee) o;
		// Objects.equals: 값이 null이어도 오류 없이 안전하게 비교해 줍니다. 네 항목이 모두 같아야(&&) true
		return Objects.equals(this.id, employee.id) && Objects.equals(this.firstName, employee.firstName)
				&& Objects.equals(this.lastName, employee.lastName) && Objects.equals(this.role, employee.role);
	}

	/**
	 * 이 객체의 "해시코드"(객체를 빠르게 찾기 위한 요약 번호)를 계산합니다.
	 * equals로 같다고 판단되는 객체는 반드시 같은 해시코드를 가져야 한다는 자바의 규칙이 있어서,
	 * equals에서 비교한 것과 같은 필드(id, 이름, 성, 역할)로 계산합니다.
	 * (HashMap, HashSet 같은 자료구조가 이 값을 사용합니다)
	 *
	 * @return 해시코드 값
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.firstName, this.lastName, this.role);
	}

	/**
	 * 이 객체를 사람이 읽기 쉬운 문자열로 바꿔 줍니다.
	 * 로그를 찍거나 디버깅할 때 유용합니다.
	 * 예) Employee{id=1, firstName='Bilbo', lastName='Baggins', role='burglar'}
	 *
	 * @return 객체 내용을 설명하는 문자열
	 */
	@Override
	public String toString() {
		// '\'' 는 작은따옴표(') 문자 하나를 뜻합니다. 값 양옆을 작은따옴표로 감싸 보기 좋게 만듭니다.
		return "Employee{" + "id=" + this.id + ", firstName='" + this.firstName + '\'' + ", lastName='" + this.lastName
				+ '\'' + ", role='" + this.role + '\'' + '}';
	}
}
