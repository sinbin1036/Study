package payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 급여(Payroll) 관리 예제 프로그램의 "시작점"이 되는 클래스입니다.
 *
 * <p>비유: 자동차의 시동 버튼입니다. 이 클래스의 main 메서드를 실행하면 스프링 부트가 켜지면서
 * 웹 서버(기본 주소 http://localhost:8080)가 뜨고, 컨트롤러·저장소·설정 클래스 등이 모두 준비됩니다.</p>
 */
// @SpringBootApplication: 스프링 부트 앱의 핵심 표시로, 다음 세 가지를 한 번에 켜 줍니다.
//   1) @Configuration      : 이 클래스도 설정 클래스로 사용
//   2) @EnableAutoConfiguration : 추가된 라이브러리를 보고 웹 서버·데이터베이스 등을 자동으로 설정
//   3) @ComponentScan      : 같은 패키지(payroll)와 그 하위에서 @RestController, @Component, @Configuration 등이
//                            붙은 클래스를 자동으로 찾아 객체(빈)로 등록
@SpringBootApplication
public class PayrollApplication {

	/**
	 * 자바 프로그램이 가장 먼저 실행하는 main 메서드입니다.
	 *
	 * <p>SpringApplication.run(...)이 스프링 부트를 가동시켜 웹 서버를 띄우고 모든 준비를 마칩니다.
	 * String... args 는 실행할 때 넘겨주는 추가 옵션들(없어도 됨)입니다.</p>
	 *
	 * @param args 실행 시 전달되는 명령줄 인자
	 */
	public static void main(String... args) {
		SpringApplication.run(PayrollApplication.class, args);
	}
}
