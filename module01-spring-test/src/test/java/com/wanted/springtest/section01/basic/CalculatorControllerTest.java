package com.wanted.springtest.section01.basic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*comment
*  @WebMvcTest 란 ?
*  - Controller 계층은 Web / Was 서버에서 오는 요청을 처리하는 웹 계층이다
*  - 해당 어노테이션은 웹 계층만 로드하여 가벼운 테스트를 수행할 수 있다
*    (Application 을  run 하면 코드 내에서 작성된 모든 빈들이 IoC 컨테이너로 들어가는데
*      이설 내가 테스트하고 싶은 빈만 등록할 수 있게 하는 어노테이션)
* */
// 컨트롤러에는 비즈니스 로직이 없어서 서비스도 필요한데 우리는 컨트롤러 하나만 빈으로 쓰기 했으니까
// 서비스로 mock 즉 가짜로 만듦

@WebMvcTest(CalculatorController.class)
@DisplayName("CalculatorController 웹 계층 테스트")
public class CalculatorControllerTest {

    /*comment
    *  MockMvc는 실제 폼켓 서버를 띄우지 않고 Http 응답/요청을 시뮬레이션 할 수 있다
    *  -> Application을 실행하지 않아도 가능하다
    *  해당 객체를 통해서 요청 처리 ,응답 생성 등을 검증할 수 있다
    *  즉 가짜 api를 쓸 수 있게 되는 것 */
    @Autowired
    private MockMvc mockMvc;

    // 테스트에서 JSON 응답을 파싱하거나 요청을 받을 때 사용한다
    @Autowired
    private ObjectMapper objectMapper;

    /*comment
    *  @MockitoBean : 가짜 객체
    *  - 실제 IoC 컨테이너에 등록된 Service를 사용하는 것이 아닌
    *    Mock(가짜) 객체를 주입한다
    *  - 이를 통해 컨트롤러 로직만 독립적으로 테스트할 수 있다 */
    @MockitoBean
    private CalculatorService calculatorService;

    //=====================================이제부터 진짜 테스트 코드 -> @Test 사용

    @Test
    @DisplayName("덧셈 API가 올바른 결과를 반환해야한다")
    void 더하기_API_올바른_결과_리턴_테스트() throws Exception {

        // given 어떤 값을 전달받냐
        int a =10, b=5, expectedSum=15;
        given(calculatorService.add(a,b)).willReturn(expectedSum);

        // when 어떤 로직이 실행되나
        // then 어떤 결과를 반환하나
        mockMvc.perform( // mockMvc로 요청 보내기
                get("/api/calculator/add") // api를 호출
                        .param("a",String.valueOf(a)) // 매개변수
                        .param("b",String.valueOf(b))
                        .contentType(MediaType.APPLICATION_JSON) // 요청을 보낼 때 "내가 보내는 데이터 형식이 JSON이야" 라고 서버에 알려주는 헤더
        )
                .andDo(print()) // 요청 및 응답 정보를 콘솔에 출력
                .andExpect(status().isOk()) // HTTP 응답 200을 기대
                // $ = JSON 데이터의 최상위 객체
                .andExpect(jsonPath("$.result").value(expectedSum))
                .andExpect(jsonPath("$.operation").value("addition"))
                .andExpect(jsonPath("$.operand1").value(a))
                .andExpect(jsonPath("$.operand2").value(b));

    }

    /*comment
    *  TestCode는 정상 동작을 하는 것을 테스트 하는 것보다는 예외 테스트를 하는 것이 더 중요하다 */
    @Test
    @DisplayName("잘못된 파라미터 타입 전송 시 GadRequest 를 반환한다")
    void 더하기_잘못된_파라미터_BadRequest_반환_테스트() throws Exception { // 메서드명 이렇게 쓰면 DisplayName 필요없음 - 이건 선택

        //when+then
        mockMvc.perform(
                get("/api/calculator/add")
                        .param("a","not_a_number")
                        .param("b",("5")) // 여기까지 컨트롤러 호출 -> 아래는 우리가 기대하는 결과
        )
                .andDo(print()) // 어떤 동작 하는지 콘솔에 출력
                .andExpect(status().isBadRequest()); // 해당 요청을 보내면 400(BadRequest) 를 기대한다


    }

}
