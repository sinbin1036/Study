package payroll;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 직원(Employee) 데이터를 데이터베이스에 저장하고 꺼내 오는 "창고 관리인" 역할의 인터페이스(리포지토리, Repository)입니다.
 *
 * <p>
 * 리포지토리는 도서관 사서에 비유할 수 있습니다. "3번 직원 정보 주세요", "이 직원 정보 보관해 주세요"라고 부탁하면
 * 사서가 서가(데이터베이스)에서 알아서 찾아 주거나 넣어 줍니다.
 * 우리는 SQL(데이터베이스에 내리는 명령어)을 직접 쓰지 않아도 됩니다.
 * </p>
 *
 * <p>
 * {@code JpaRepository<Employee, Long>}을 상속(extends)하기만 하면,
 * 스프링 데이터 JPA(JPA: 자바 객체와 데이터베이스 표(table)를 자동으로 연결해 주는 표준 기술)가
 * 실행 시점에 실제 구현을 자동으로 만들어 줍니다. 그래서 몸통이 비어 있어도
 * {@code save}(저장), {@code findById}(번호로 찾기), {@code findAll}(전체 조회),
 * {@code deleteById}(번호로 삭제) 같은 기능을 바로 쓸 수 있습니다.
 * </p>
 * <p>
 * 제네릭 {@code <Employee, Long>}의 뜻: 관리할 대상은 Employee(직원) 엔티티이고, 그 고유 번호(id)의 타입은 Long(큰 정수)이라는 의미입니다.
 * </p>
 */
interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
