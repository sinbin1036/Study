package payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 이 애플리케이션(프로그램)의 "시작 버튼" 역할을 하는 클래스입니다.
 * <p>
 * 이 프로젝트는 "급여 관리(payroll)" 시스템의 아주 간단한 예제로,
 * 직원(Employee) 정보를 인터넷 주소(URL)로 조회·추가·수정·삭제할 수 있게 해 주는
 * REST API 서버입니다.
 * (REST API: 웹 주소와 HTTP 메서드(GET, POST, PUT, DELETE 등 "요청의 종류")를 조합해서
 *  다른 프로그램이 데이터를 주고받을 수 있게 만든 약속된 방식)
 * <p>
 * {@code @SpringBootApplication} 어노테이션(@로 시작하는 "꼬리표", 프로그램에게 주는 설명서 같은 것)은
 * 스프링 부트에게 다음 세 가지를 한꺼번에 부탁하는 표시입니다.
 * <ul>
 *   <li>이 클래스가 "설정의 출발점"이라는 것을 알려 줌</li>
 *   <li>필요한 기능(웹 서버, 데이터베이스 연결 등)을 라이브러리 목록을 보고 알아서 자동 설정해 줌</li>
 *   <li>같은 패키지(payroll 폴더)와 그 하위에 있는 컨트롤러·설정 클래스 등을 스스로 찾아서 등록해 줌</li>
 * </ul>
 * 비유하자면, 가게 문을 열 때 "전기 켜고, 직원 출근시키고, 간판 불 켜기"를
 * 한 번에 해 주는 마스터 스위치와 같습니다.
 */
@SpringBootApplication
public class PayrollApplication {

	/**
	 * 자바 프로그램이 실행될 때 가장 먼저 호출되는 메서드(프로그램의 입구)입니다.
	 * <p>
	 * {@code SpringApplication.run(...)}을 호출하면 스프링 부트가
	 * 내장 웹 서버(기본적으로 Tomcat, 보통 8080번 포트)를 켜고,
	 * 데이터베이스를 준비하고, 이 프로젝트의 모든 부품(빈, Bean: 스프링이 대신 만들어서 관리해 주는 객체)을
	 * 만들어 서로 연결한 뒤, 요청을 받을 준비를 마칩니다.
	 *
	 * @param args 프로그램 실행 시 명령줄에서 넘겨줄 수 있는 추가 옵션들
	 *             ({@code String...}은 "문자열을 0개 이상 여러 개 받을 수 있다"는 뜻)
	 */
	public static void main(String... args) {
		// 스프링 부트 애플리케이션을 실행합니다. (이 한 줄로 서버 전체가 켜집니다)
		SpringApplication.run(PayrollApplication.class, args);
	}
}
