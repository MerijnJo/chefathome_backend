package com.example.chefbackend;

import com.example.chefbackend.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class ChefbackendApplicationTests {

    @Test
    void contextLoads() {
    }
}
