package top.yangyuscript.knowledge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.yangyuscript.knowledge.common.Result;
import top.yangyuscript.knowledge.model.request.UserRequest;
import top.yangyuscript.knowledge.model.request.UserVO;
import top.yangyuscript.knowledge.service.UserService;
import top.yangyuscript.knowledge.utils.EmailUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 15:49 2020/8/8
 */
@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailUtil emailUtil;

    @PostMapping("/register")
    @ResponseBody
    public Result<String> register(UserRequest userRequest) {
        Pair<Boolean, String> pair = userService.regist(userRequest);
        return Result.build(pair);
    }

    @PostMapping("/login")
    @ResponseBody
    public Result<UserVO> login(UserRequest userRequest) {
        String token = userService.login(userRequest.getEmail(), userRequest.getPassword());
        boolean flag = StringUtils.isEmpty(token);
        return Result.build(Pair.of(!flag, !flag ? "登录成功" : "请先注册账号"), new UserVO(userRequest.getEmail(),token));
    }

    @PostMapping("/logout")
    @ResponseBody
    public Result<Boolean> logout(UserRequest userRequest, @RequestHeader(name = "token")String token) {
        boolean flag = userService.logout(userRequest.getEmail(), token);
        return Result.build(Pair.of(flag, flag ? "登出成功" : "登出失败"), flag);
    }

    @PostMapping("/getCheckCode")
    @ResponseBody
    public Map<String,Boolean> getCheckCode(@RequestParam(name = "emailTo")String emailTo){
        boolean flag = emailUtil.sendRegisterMail(emailTo);
        Map<String,Boolean> map = new HashMap<>();
        map.put("data",flag);
        return map;
    }

    @PostMapping("/forgetPass")
    @ResponseBody
    public Result<Boolean> forgetPass(UserRequest userRequest) {
        String password = userService.forgetPass(userRequest.getEmail());
        if(StringUtils.isEmpty(password)){
            return Result.success(false);
        }
        boolean flag = emailUtil.sendPasswordEmail(userRequest.getEmail(),password);
        return Result.success(flag);
    }
}
