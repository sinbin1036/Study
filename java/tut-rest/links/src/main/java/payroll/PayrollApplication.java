package payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 이 애플리케이션(프로그램)의 "시작 버튼" 역할을 하는 클래스입니다.
 *
 * <p>
 * 이 프로젝트(links 모듈)는 "직원(Employee)"과 "주문(Order)"을 관리하는 간단한 REST API 서버입니다.
 * (REST API: 웹 주소(URL)와 HTTP 메서드(GET/POST/PUT/DELETE 등)를 이용해
 * 다른 프로그램이 데이터를 조회·생성·수정·삭제할 수 있게 해 주는 창구)
 * </p>
 *
 * <p>
 * 특히 이 모듈은 "상태에 따라 바뀌는 하이퍼미디어 링크(HATEOAS)"를 보여 줍니다.
 * (하이퍼미디어 링크: 응답 데이터 안에 "다음에 할 수 있는 행동"의 주소를 함께 담아 주는 것.
 * 웹 페이지의 버튼/링크처럼, 클라이언트가 응답만 보고 다음 행동을 고를 수 있게 해 줍니다.)
 * 예를 들어 "진행 중"인 주문에는 "취소(cancel)"와 "완료(complete)" 링크가 붙지만,
 * 이미 완료되었거나 취소된 주문에는 그런 링크가 붙지 않습니다.
 * </p>
 *
 * <p>
 * {@code @SpringBootApplication} 어노테이션(코드에 붙이는 "설명 스티커" 같은 표시)은
 * 스프링 부트에게 "이 클래스가 있는 패키지(payroll)부터 필요한 부품들을 알아서 찾아 조립하고,
 * 웹 서버·데이터베이스 같은 기본 설정도 자동으로 해 줘"라고 알려 줍니다.
 * 즉, 세 가지 기능(@Configuration + @EnableAutoConfiguration + @ComponentScan)을 한 번에 켜는 스위치입니다.
 * </p>
 */
@SpringBootApplication
public class PayrollApplication {

	/**
	 * 자바 프로그램이 실행될 때 가장 먼저 호출되는 메서드(main 메서드)입니다.
	 *
	 * <p>
	 * {@code SpringApplication.run(...)}을 호출하면 스프링 부트가
	 * 내장 웹 서버(기본값: 톰캣, 8080 포트)를 켜고, 이 프로젝트의 컨트롤러·리포지토리 등
	 * 모든 부품(빈, Bean: 스프링이 대신 만들어 관리해 주는 객체)을 준비한 뒤 요청을 기다립니다.
	 * </p>
	 *
	 * @param args 프로그램 실행 시 명령줄에서 전달되는 인자들 (String... 은 "개수가 정해지지 않은 문자열 여러 개"라는 뜻)
	 */
	public static void main(String... args) {
		SpringApplication.run(PayrollApplication.class, args);
	}
}
