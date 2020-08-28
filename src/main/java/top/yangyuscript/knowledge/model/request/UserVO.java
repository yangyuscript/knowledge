package top.yangyuscript.knowledge.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 17:04 2020/8/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private String email;
    private String token;
}
