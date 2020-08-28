package top.yangyuscript.knowledge.model;

import lombok.Data;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 15:51 2020/8/8
 */
@Data
public class User {
    private String id;
    private String email;
    private String name;
    private String registerTime;
    private String password;
}
