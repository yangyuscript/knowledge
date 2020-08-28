package top.yangyuscript.knowledge.common;

public class ParamKey {
    public static final String MIND = "mind";
    public static final String MIND_PICTURE = "mind_picture_";
    public static final String CREATE_TIME = "createTime";
    public static final String MODIFY_TIME = "modifyTime";
    public static final String MID = "mid";
    public static final String RANK_MINDS = "rank_minds";
    public static final long TWO_DAYS = 1000*60*60*2;
    public static final String USER_MIND_MAP = "user_mind_map";
    /**
     * 脑图id与topic映射键，用于topic模糊搜索
     */
    public static final String MINDMAPS_NAME_ID_MAP = "mindmaps_name_id_map";

    /**
     * 用户key前缀
     */
    public static final String MIND_USER_PREFIX = "mind_user_";

    /**
     * token前缀
     */
    public static final String MIND_TOKEN_PREFIX = "mind_token_";

    /**
     * 邮箱注册验证码
     */
    public static final String MIND_REGISTER_PREFIX = "mind_email_register_";

    /**
     * 邮箱找回密码
     */
    public static final String MIND_FORGET_PASSWORD_PREFIX = "mind_forget_password_";
}
