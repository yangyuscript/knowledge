package top.yangyuscript.knowledge.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 17:01 2020/8/22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailUtilTest {

    @Autowired
    private EmailUtil emailUtil;

    @Test
    public void sendMail() {
        emailUtil.sendMail("1837475870@qq.com");
    }
}