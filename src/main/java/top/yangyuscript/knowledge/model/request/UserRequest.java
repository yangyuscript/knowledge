package top.yangyuscript.knowledge.model.request;

import lombok.Data;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 16:12 2020/8/8
 */
@Data
public class UserRequest {
    private String id;
    private String email;
    private String password;
    private String confirmPass;
    private String checkCode;
}
