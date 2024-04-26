package com.eastwind;

import com.eastwind.service.impl.TestServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TakeOutApplicationTests {

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void contextLoads() {
        testService.test();
    }

}
