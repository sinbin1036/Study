package payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 급여(Payroll) 관리 애플리케이션의 "시작 버튼" 역할을 하는 클래스입니다.
 *
 * <p>프로그램을 실행하면 가장 먼저 이 클래스의 main 메서드가 호출되고,
 * 여기서 스프링 부트(Spring Boot, 자바로 웹 서버를 쉽게 만들 수 있게 도와주는 도구 모음)가 켜집니다.
 * 자동차에 비유하면 시동 키를 돌리는 곳이라고 생각하면 됩니다.</p>
 *
 * <p>{@code @SpringBootApplication} 어노테이션(annotation, 코드에 붙이는 "메모/표식"으로
 * 스프링에게 특별한 처리를 부탁하는 역할)은 다음 세 가지를 한 번에 켜 줍니다.</p>
 * <ul>
 *   <li>설정 클래스로 인식하기 (이 클래스 안의 설정을 읽어 들임)</li>
 *   <li>자동 설정 (필요한 라이브러리를 보고 웹 서버, 데이터베이스 연결 등을 알아서 준비함)</li>
 *   <li>컴포넌트 스캔 (같은 패키지 {@code payroll} 안의 컨트롤러, 설정 클래스 등을 자동으로 찾아서 등록함)</li>
 * </ul>
 */
@SpringBootApplication
public class PayrollApplication {

	/**
	 * 자바 프로그램의 진입점(프로그램이 실행될 때 가장 먼저 실행되는 메서드)입니다.
	 *
	 * <p>{@code SpringApplication.run(...)}을 호출하면 스프링이 내장 웹 서버를 띄우고,
	 * 필요한 객체(빈, Bean: 스프링이 대신 만들어 관리해 주는 객체)들을 생성·연결한 뒤
	 * 외부에서 들어오는 HTTP 요청(웹 브라우저나 프로그램이 서버에 보내는 요청)을 기다리기 시작합니다.</p>
	 *
	 * @param args 프로그램 실행 시 명령줄에서 넘겨받는 인자들 (보통은 비어 있음).
	 *             {@code String...}은 "문자열을 여러 개 받을 수 있다"는 뜻의 가변 인자 문법입니다.
	 */
	public static void main(String... args) {
		SpringApplication.run(PayrollApplication.class, args);
	}
}
