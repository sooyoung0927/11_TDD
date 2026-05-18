package com.wanted.springtest.section02.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*comment
*  Repository 계층 테스트
*  @DataJpaTest
*  - 핵심 기능만 간소화 하여 Spring Data Jpa의 동작을 테스트한다
*  */
@DataJpaTest
@ActiveProfiles("test")
public class CalculationHistoryRepositoryTest {

    // @DataJpaTest 가 자동으로 Repository를 Bean으로 등록한다
    @Autowired
    private CalculationHistoryRepository repository;

    @Test
    void 계산기록_저장_및_조회_테스트(){
        // given 계산 기록 생성
        CalculationHistory history = new CalculationHistory("ADD",10.0,5.0,15.0) ;

        // when 계산 기록 저장 후 조회
        CalculationHistory savedHistory =
                repository.save(history);

        CalculationHistory foundHistory =
                repository.findById(savedHistory.getId()).orElseThrow();

        // then
        assertNotNull(foundHistory);
    }

    @Test
    void 연산_종류로_계산_기록_조회하기(){
        // given
        repository.save(new CalculationHistory("ADD",1.0,2.0,3.0));
        repository.save(new CalculationHistory("MULTIPLY",3.0,4.0,12.0));
        repository.save(new CalculationHistory("ADD",5.0,6.0,11.0));

        //when
        // 갯수 세기
        List<CalculationHistory> addRecords = repository.findByOperation("ADD");

        //then
        // addRecords가 2개의 값을 가지고 있는지 검증
        assertEquals(2,addRecords.size());
        // addRecords의 operation 필드가 ADD인지 검증
        addRecords.forEach(record -> assertEquals("ADD", record.getOperation()));

    }


}
