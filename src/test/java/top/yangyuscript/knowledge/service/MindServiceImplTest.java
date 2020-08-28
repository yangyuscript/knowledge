package top.yangyuscript.knowledge.service;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.test.context.junit4.SpringRunner;
import top.yangyuscript.knowledge.common.ParamKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 18:09 2020/4/19
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MindServiceImplTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void save() {
        GenericJackson2JsonRedisSerializer s = new GenericJackson2JsonRedisSerializer();
        //redisTemplate.delete(ParamKey.MINDMAPS_NAME_ID_MAP);
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"脑图之家功能","mind_picture_202003152");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"Java技能","mind_picture_202003161");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"消息队列","mind_picture_202003191");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"互联网技术学习方法","mind_picture_202003192");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"脑图之家技术栈","mind_picture_202003221");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"redis集群","mind_picture_202003222");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"java异常","mind_picture_202003231");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"java io","mind_picture_202003241");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"java开发面试","mind_picture_202003242");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"文件系统","mind_picture_202003251");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"redis数据类型及常用方法","mind_picture_202003281");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"微服务基础架构","mind_picture_202003291");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"JVM","mind_picture_202003301");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"常见返回结果的HTTP状态码","mind_picture_202003311");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"java阻塞队列","mind_picture_202004021");
        redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,"负载均衡常用算法","mind_picture_202004131");
    }

    @Test
    public void test(){
        Set<String> set = redisTemplate.keys("mind_picture_*");
        for(String str:set){
            String mindStr = (String)redisTemplate.opsForValue().get(str);
            Map<String,String> mind = (Map)JSON.parse(mindStr);
            mind.put("email","1837475870@qq.com");
            String mid = mind.get("mid");
            redisTemplate.opsForValue().set(mid,JSON.toJSONString(mind));

            List<String> list = (List)JSON.parse((String)redisTemplate.opsForHash().get(ParamKey.USER_MIND_MAP,"1837475870@qq.com"));
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(mid);
            redisTemplate.opsForHash().put(ParamKey.USER_MIND_MAP,"1837475870@qq.com",JSON.toJSONString(list));
        }
    }
}