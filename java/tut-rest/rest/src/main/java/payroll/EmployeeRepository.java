package payroll;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 직원(Employee) 데이터를 데이터베이스에 저장하고 꺼내 오는 "창고 관리인" 역할의 인터페이스입니다.
 * <p>
 * 리포지토리(Repository)란 "저장소"라는 뜻으로, 데이터베이스와 직접 대화하는 일을 전담합니다.
 * 비유하면 도서관 사서처럼 "이 책 좀 보관해 주세요", "3번 책 찾아 주세요" 같은 부탁을 대신 처리해 줍니다.
 * <p>
 * 신기하게도 이 인터페이스 안에는 코드가 한 줄도 없습니다.
 * {@code JpaRepository<Employee, Long>}을 상속(extends: 부모의 기능을 물려받음)하기만 하면,
 * 스프링 데이터 JPA가 실행 시점에 실제 동작하는 구현체를 자동으로 만들어 줍니다.
 * (JPA: 자바 객체와 데이터베이스 표(테이블)를 자동으로 연결해 주는 자바 표준 기술.
 *  SQL 문장을 직접 쓰지 않아도 객체를 저장/조회할 수 있게 해 줍니다)
 * <p>
 * 꺾쇠괄호 안의 두 값의 의미:
 * <ul>
 *   <li>{@code Employee} : 이 창고가 다루는 물건의 종류(직원 엔티티)</li>
 *   <li>{@code Long} : 각 물건을 구별하는 번호표(기본 키, id)의 자료형(큰 정수)</li>
 * </ul>
 * 이렇게만 선언해도 아래와 같은 메서드를 바로 쓸 수 있습니다. (이 프로젝트에서 실제로 사용하는 것들)
 * <ul>
 *   <li>{@code findAll()} : 모든 직원 조회</li>
 *   <li>{@code findById(id)} : 번호(id)로 직원 한 명 조회 (결과가 없을 수도 있어서 Optional로 감싸서 돌려줌)</li>
 *   <li>{@code save(employee)} : 직원 저장(새로 추가하거나, 이미 있으면 수정)</li>
 *   <li>{@code deleteById(id)} : 번호(id)로 직원 삭제</li>
 * </ul>
 */
interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
