package com.shoppy.admin.user;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PwdEncoderTest {
    @Test
    public void testEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPwd = encoder.encode("myPass");
        System.out.println(encodedPwd);
        Assertions.assertThat(encoder.matches("myPass",encodedPwd)).isTrue();
    }
}
