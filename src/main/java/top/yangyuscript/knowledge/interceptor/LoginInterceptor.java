package top.yangyuscript.knowledge.interceptor;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import top.yangyuscript.knowledge.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 15:35 2020/8/15
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    public static void main(String[] args) {
        String a = "getCheckCode";
        System.out.println(a.contains("getCheckCode"));
    }

    /*private static final List<String> EXCLUDE_PATH_LIST = Arrays.asList("search","create","login"
            ,"register","detail","getCheckCode","forgetPass","index","/js/","/style/","/picture/","/dist/");*/

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*String uri = request.getRequestURI();
        if(uri.trim().equals("") || uri.trim().equals("/")){
            return true;
        }
        for (int i = 0; i < EXCLUDE_PATH_LIST.size(); i++) {
            if(uri.contains(EXCLUDE_PATH_LIST.get(i))){
                return true;
            }
        }*/

        String token = request.getHeader("token");
        if(StringUtils.isEmpty(token)){
            token = request.getParameter("token");
            if(StringUtils.isEmpty(token)){
                //response.sendRedirect("/?needLogin=true");
                response.setHeader("needLogin","true");
                return false;
            }
        }

        String email = request.getParameter("email");
        String userStr = stringRedisTemplate.opsForValue().get(token);
        User user = JSON.parseObject(userStr,User.class);
        if(user == null || !email.equals(user.getEmail())){
            //response.sendRedirect("/?needLogin=true");
            response.setHeader("needLogin","true");
            return false;
        }
        stringRedisTemplate.expire(token, 2, TimeUnit.HOURS);
        return true;
    }
}
