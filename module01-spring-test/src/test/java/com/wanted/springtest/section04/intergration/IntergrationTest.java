package com.wanted.springtest.section04.intergration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*comment
*  @SpringBootTest
*  - Spring의 전체 컨텍스트를 로딩한다
*    = IoC 컨테이너를 로딩한다
*  - 통합 테스트 시에 사용한다 */
@SpringBootTest
public class IntergrationTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setup(){
        // 실제로는 나중에 @Autowired로 가져옴
        // 지금은 학습 편의상 빈으로 등록하지 않았기 때문에 mock 객체로 만듦
        userRepository = mock(UserRepository.class);

        // 서비스가 생성되는 시점에 Mock 주입
        userService = new UserService(userRepository);
    }

    @Test
    void 이메일_중복_시_예외_발생_테스트(){
        // given : 중복 이메일 상황 설정
        User newUser = new User("김철수","test@example.com");
        // 해당 이메일이 존재한다고 강제로 설정
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Service에서 Throw 한 예외가 잘 발생하는지 테스트
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                ()-> userService.createUser(newUser)
        );

        //then
        // 앞에 거 : 예상 결과 랑 뒤에 거 : 실제 결과랑 같은지 확인
        assertEquals("이미 존재하는 이메일입니다: test@example.com", exception.getMessage());
    }

//    =================================================

    @Test
    void 유효한_ID로_사용자_조회_테스트(){

        // given : 사용자 데이터 설정
        Long userId = 1L;
        User expectUser = new User(userId,"raccoon","raccoon@test.com",true);

        // 사용자가 유효하다고 강제화
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectUser));
        // .of : ~로
        // .of(expectUser) : expectUser로 이루어져 있다
        //   -> Optional<T> 에서 T가 User가 됨

        // when
        Optional<User> actualUser = userService.findUserById(userId);

        // then
        assertEquals("raccoon",actualUser.get().getName());
        assertEquals("raccoon@test.com",actualUser.get().getEmail());

    }


    @Test
    void 유효하지_않은_ID로_사용자_조회_테스트(){

        // given : 사용자 데이터 설정
        Long invalidId = 999L;

        //empty() : 비어있다는 의미
        // 사용자가 없다고 강제화
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        // when
        Optional<User> result = userService.findUserById(invalidId);

        // then
        // isPresent() : 존재한다
        // 실제로 존재하고 있지 않음 -> isPresent() false 나옴
        assertFalse(result.isPresent());

    }

    @Test
    void 존재하지_않는_사용자_비활성화_시_예외_발생_테스트(){
        // 존재하지 않는 사용자
        Long nonExist = 999L;
        when(userRepository.findById(nonExist)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                // 기대하는 예외 클래스 타입
                IllegalArgumentException.class,
                // 예외 발생 메서드
                ()-> userService.deactivateUser(nonExist)
        );

        // then 검증
        assertEquals("사용자를 찾을 수 없습니다: "+nonExist, exception.getMessage());

    }


}
