package top.yangyuscript.knowledge.service;


import org.springframework.data.util.Pair;
import top.yangyuscript.knowledge.model.request.UserRequest;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 15:50 2020/8/8
 */
public interface UserService {
    Pair<Boolean,String> regist(UserRequest userRequest);
    String login(String email, String password);
    boolean logout(String email, String token);
    String forgetPass(String email);
}
