package top.yangyuscript.knowledge.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.yangyuscript.knowledge.common.ParamKey;
import top.yangyuscript.knowledge.model.User;
import top.yangyuscript.knowledge.model.request.UserRequest;
import top.yangyuscript.knowledge.utils.TimeUtil;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 15:57 2020/8/8
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Pair<Boolean,String> regist(UserRequest userRequest) {
        String checkCode = userRequest.getCheckCode();
        String realCheckCode = stringRedisTemplate.opsForValue().get(ParamKey.MIND_REGISTER_PREFIX + userRequest.getEmail());
        if(StringUtils.isEmpty(checkCode) || !checkCode.equals(realCheckCode)){
            return Pair.of(false,"验证码不正确");
        }
        String userStr = stringRedisTemplate.opsForValue().get(ParamKey.MIND_USER_PREFIX + userRequest.getEmail());
        if (!StringUtils.isEmpty(userStr)) {
            return Pair.of(false,"邮箱已存在");
        }
        if (userRequest.getPassword().equals(userRequest.getConfirmPass())) {
            User user = new User();
            BeanUtils.copyProperties(userRequest, user);
            user.setId(UUID.randomUUID().toString());
            user.setRegisterTime(TimeUtil.getNowTime());
            user.setPassword(user.getPassword());
            stringRedisTemplate.opsForValue().set(ParamKey.MIND_USER_PREFIX + user.getEmail(), JSON.toJSONString(user));
            return Pair.of(true,"注册成功");
        }
        return Pair.of(false,"注册失败");
    }

    @Override
    public String login(String email, String password) {
        String userStr = stringRedisTemplate.opsForValue().get(ParamKey.MIND_USER_PREFIX + email);
        if (!StringUtils.isEmpty(userStr)) {
            User user = JSON.parseObject(userStr, User.class);
            if (user.getPassword().equals(password)) {
                String token = ParamKey.MIND_TOKEN_PREFIX + UUID.randomUUID().toString();
                stringRedisTemplate.opsForValue().set(token,userStr);
                stringRedisTemplate.expire(token,2, TimeUnit.HOURS);
                return token;
            }
        }
        return null;
    }

    @Override
    public boolean logout(String email, String token) {
        String userStr = stringRedisTemplate.opsForValue().get(token);
        if (!StringUtils.isEmpty(userStr)) {
            User user = JSON.parseObject(userStr, User.class);
            if (user.getEmail().equals(email)) {
                return stringRedisTemplate.delete(token);
            }
        }
        return false;
    }

    @Override
    public String forgetPass(String email) {
        String userStr = stringRedisTemplate.opsForValue().get(ParamKey.MIND_USER_PREFIX + email);
        if (!StringUtils.isEmpty(userStr)) {
            User user = JSON.parseObject(userStr, User.class);
            return user.getPassword();
        }
        return null;
    }
}
