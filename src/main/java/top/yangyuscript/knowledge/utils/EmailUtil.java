package top.yangyuscript.knowledge.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.yangyuscript.knowledge.common.ParamKey;

import java.util.concurrent.TimeUnit;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 16:57 2020/8/22
 */
@Component
public class EmailUtil {
    @Autowired(required = false)
    private JavaMailSender sender;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String emailTo) {
        SimpleMailMessage mimeMessage = new SimpleMailMessage();
        mimeMessage.setFrom("MIND-HUP<"+ from + ">");
        mimeMessage.setTo(emailTo);
        mimeMessage.setSubject("SpringBoot集成JavaMail实现邮件发送");
        mimeMessage.setText("SpringBoot集成JavaMail实现邮件发送正文");
        sender.send(mimeMessage);
    }

    public boolean sendRegisterMail(String emailTo) {
        String registerKey = ParamKey.MIND_REGISTER_PREFIX + emailTo;
        String registerVal = stringRedisTemplate.opsForValue().get(registerKey);
        if(!StringUtils.isEmpty(registerVal)){
            return false;
        }
        SimpleMailMessage mimeMessage = new SimpleMailMessage();
        mimeMessage.setFrom("MIND-HUP<"+ from + ">");
        mimeMessage.setTo(emailTo);
        mimeMessage.setSubject("Mindhup-脑图小站 验证码");
        int registerCode = RandomNumUtil.getRandom4Num();
        stringRedisTemplate.opsForValue().set(registerKey,String.valueOf(registerCode));
        stringRedisTemplate.expire(registerKey,3*60, TimeUnit.SECONDS);
        mimeMessage.setText("您的注册验证码是" + registerCode + "，有效时间3分钟");
        sender.send(mimeMessage);
        return true;
    }

    public boolean sendPasswordEmail(String emailTo,String password) {
        String registerKey = ParamKey.MIND_FORGET_PASSWORD_PREFIX + emailTo;
        String registerVal = stringRedisTemplate.opsForValue().get(registerKey);
        if(!StringUtils.isEmpty(registerVal)){
            return true;
        }
        SimpleMailMessage mimeMessage = new SimpleMailMessage();
        mimeMessage.setFrom("MIND-HUP<"+ from + ">");
        mimeMessage.setTo(emailTo);
        mimeMessage.setSubject("Mindhup-脑图小站 找回密码");
        stringRedisTemplate.opsForValue().set(registerKey, password);
        stringRedisTemplate.expire(registerKey,3*60, TimeUnit.SECONDS);
        mimeMessage.setText("您的登录密码是" + password + "，请妥善保存，以免密码泄露");
        sender.send(mimeMessage);
        return true;
    }

}
