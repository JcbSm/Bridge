package com.github.jcbsm.bridge.util;

import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestUtilTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    @DisplayName("C1_HttpRequestUtil_Get")
    class GetTests {

        @Test
        @DisplayName("T1_HttpRequestUtil_Get_")
        void getTest() {

            try {
                String res = HttpRequestUtil.get("https://api.mojang.com/users/profiles/minecraft/JcbSm");
                System.out.println(res);
            } catch (IOException e) {
                fail("Error thrown.", e);
            }
        }
    }
}