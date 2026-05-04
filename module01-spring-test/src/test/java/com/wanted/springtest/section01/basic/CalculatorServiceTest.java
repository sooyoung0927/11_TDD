package com.wanted.springtest.section01.basic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/*comment
*  @SpringBootTest 란 ?
*  - Application 전체를 로드하여 통합 테스트 환경을 제공한다
*  - IoC 컨테이너 내부의 모든 빈들을 이용하기 위행 가져오는 어노테이션
*  @ActiveProfiles 란 ?
*  - 운영환경/ 개발환경/테스트환경 등 각각의 환경에 따라 다른 설정을 적용할 수 있도록 한다
*  - 실무에서 yml 사용방법 (application으로 접두어 통일)
*  application-dev.yml 개발 시
*  application-test.yml 테스트 환경
*  application-prod.yml 운영 환경
* */

@SpringBootTest
@ActiveProfiles("test") // 이렇게 써주면 test 코드쪽에 있는 yml 사용
public class CalculatorServiceTest {

    /*comment
    *  @ActiveProfiles("test") 어노테이션으로 인해
    *  TestConfig 클래스에 정의한 testCalculatorService() Bean이 주입된다
    * */
    @Autowired
    private CalculatorService calculatorService;

    /*comment
    *  Service 계층 테스트는 실제 비즈니스 로직의 동작을 확인하는 용도로 사용된다*/
    @Test
    void 나눗셈_올바르게_동작하는지_테스트(){
        // given
        int a =15, b=3, expected=5;

        // when
        int actual = calculatorService.divide(a,b);

        // then
        assertEquals(expected,actual);
    }

    @Test
    void 나눗셈을_0으로_나누면_ArithmeticException_발생_테스트(){
        // given
        int a =10,b=0;

        // when then
        // 단순 예외가 발생하면서 통과시키는 게 아니라
        // 예외가 발생 + 어떤 예외 클래스가 동작하는 지도 확인
        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> calculatorService.divide(a,b)
        );

    }
}
