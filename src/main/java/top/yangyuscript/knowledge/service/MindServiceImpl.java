package top.yangyuscript.knowledge.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import top.yangyuscript.knowledge.common.ParamKey;
import top.yangyuscript.knowledge.utils.TimeUtil;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("mindService")
public class MindServiceImpl implements MindService{
    private static final Logger Log = LoggerFactory.getLogger(MindServiceImpl.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String save(String mindStr,String email, String tags) {
        Map<String,String> map = new HashMap<>();
        String pre = ParamKey.MIND_PICTURE + TimeUtil.getTime("yyyyMMdd");
        long key = redisTemplate.opsForValue().increment("num_"+pre,1);
        String mid = pre+String.valueOf(key);
        map.put("mid",mid);
        Map<String,Object> mind = JSONObject.parseObject(mindStr);
        mind.put("mid",mid);
        mind.put("email",email);
        resolveTags(mind,tags);
        mind.put(ParamKey.CREATE_TIME,TimeUtil.getNowTime());
        String realMindStr = JSONObject.toJSONString(mind);

        List<String> list = (List)JSON.parse((String)redisTemplate.opsForHash().get(ParamKey.USER_MIND_MAP,email));
        if(list == null){
            list = new ArrayList<>();
        }
        final List<String> targetList = list;

        SessionCallback<Object> callback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 注意:multi和exec方法间执行查询操作无效
                redisOperations.multi();
                redisOperations.opsForList().leftPush(ParamKey.MIND,mid);
                redisOperations.opsForValue().set(mid,realMindStr);
                redisOperations.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,((Map)mind.get("data")).get("topic"),mid);
                targetList.add(mid);
                redisOperations.opsForHash().put(ParamKey.USER_MIND_MAP,email,JSON.toJSONString(targetList));
                return redisOperations.exec();
            }
        };
        redisTemplate.execute(callback);
        return map.get("mid");
    }

    @Override
    public String update(String mid,String mindStr,String tags) {
        String originalMindStr = get(mid);
        Map<String,Object> originalMindMap = JSONObject.parseObject(originalMindStr);
        String originalTopicStr = (String)((Map)originalMindMap.get("data")).get("topic");
        Map<String,Object> nowMindMap = JSONObject.parseObject(mindStr);
        String nowTopicStr = (String)((Map)nowMindMap.get("data")).get("topic");
        nowMindMap.put(ParamKey.CREATE_TIME,originalMindMap.get(ParamKey.CREATE_TIME));
        nowMindMap.put(ParamKey.MODIFY_TIME,TimeUtil.getNowTime());
        nowMindMap.put(ParamKey.MID,mid);
        resolveTags(nowMindMap,tags);
        mindStr = JSONObject.toJSONString(nowMindMap);
        if(!originalTopicStr.equals(nowTopicStr)){
            redisTemplate.opsForHash().put(ParamKey.MINDMAPS_NAME_ID_MAP,nowTopicStr,mid);
            redisTemplate.opsForHash().delete(ParamKey.MINDMAPS_NAME_ID_MAP,originalTopicStr);
        }
        redisTemplate.opsForValue().set(mid,mindStr);
        return mid;
    }

    @Override
    public List<String> get(int len) {
        List<String> list = redisTemplate.opsForList().range(ParamKey.MIND,0,len-1);
        return list;
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Set<String> getSet(int start,int end, String key) {
        Set<String> set = redisTemplate.opsForZSet().reverseRange(key,start, end);
        return set;
    }

    @Override
    public Set<Object> scan(String topic){
        Set<Object> sets = redisTemplate.execute((RedisConnection redisConnection) -> {
            Set<Object> binaryKeys = new HashSet<>();
            Cursor<Map.Entry<byte[],byte[]>> cursors = redisConnection.hScan(ParamKey.MINDMAPS_NAME_ID_MAP.getBytes(),new ScanOptions.ScanOptionsBuilder().match("*"+topic+"*").count(1000).build());
            while (cursors.hasNext()){
                try {
                    binaryKeys.add(new String(cursors.next().getValue(),"UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    Log.error("scan异常");
                }
            }
            return binaryKeys;
        });
        return sets;
    }

    @Override
    public boolean addClick(String mid, String ip) {
        String key = TimeUtil.getTime("yyyyMMdd")+mid+ip;
        boolean flag = redisTemplate.opsForValue().setIfAbsent(key,"1");
        if(flag){
            redisTemplate.expire(key,1, TimeUnit.DAYS);
            redisTemplate.opsForZSet().incrementScore(ParamKey.RANK_MINDS,mid,1);
        }
        return flag;
    }

    @Override
    public List<String> getUserMindMap(String email) {
        return (List)JSON.parse((String)redisTemplate.opsForHash().get(ParamKey.USER_MIND_MAP,email));
    }

    private String getMid4Dtl(String mid, String topic){
        return mid+"_dtl_"+topic;
    }

    private void resolveTags(Map<String,Object> mind,String tags){
        if(!StringUtils.isEmpty(tags)){
            List<String> tagsList = Arrays.stream(tags.split(",")).filter(t -> !StringUtils.isEmpty(t)&&!StringUtils.isEmpty(t.trim())).collect(Collectors.toList());
            mind.put("tags",tagsList);
        }
    }
}
