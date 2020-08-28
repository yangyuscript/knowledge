package top.yangyuscript.knowledge.service;

import java.util.List;
import java.util.Set;

public interface MindService {
    String save(String mindStr,String email);
    String update(String mid,String mindStr);
    List<String> get(int len);
    String get(String key);
    Set<Object> scan(String topic);
    Set<String> getSet(int start,int end,String key);
    boolean addClick(String mid,String ip);
    List<String> getUserMindMap(String email);
}
