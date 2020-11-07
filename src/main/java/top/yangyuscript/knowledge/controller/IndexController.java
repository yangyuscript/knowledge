package top.yangyuscript.knowledge.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import top.yangyuscript.knowledge.common.ParamKey;
import top.yangyuscript.knowledge.service.MindService;
import top.yangyuscript.knowledge.service.UserService;
import top.yangyuscript.knowledge.utils.EmailUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class IndexController {

    @Autowired
    private MindService mindService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailUtil emailUtil;

    @RequestMapping(value = "",method = RequestMethod.GET)
    public String indexOne(Model model,@RequestParam(name = "needLogin",required = false)boolean needLogin){
        return index(model,needLogin);
    }

    @RequestMapping(value = "index",method = RequestMethod.GET)
    public String index(Model model, @RequestParam(name = "needLogin",required = false)boolean needLogin){
        if(needLogin){
            model.addAttribute("needLogin","true");
        }

        List<String> minds = mindService.get(10);
        List<Map<String,Object>> mindsListMap = new ArrayList<>();
        if(null != minds && minds.size() > 0){
            for (String str:minds) {
                String ret = mindService.get(str);
                mindsListMap.add((Map)JSON.parse(ret));
            }
        }
        model.addAttribute("latestMinds",mindsListMap);

        Set<String> clickRankSet = mindService.getSet(0,9,ParamKey.RANK_MINDS);
        List<Map<String,Object>> hotMinds = new ArrayList<>();
        for (String mindStr:clickRankSet) {
            String ret = mindService.get(mindStr);
            hotMinds.add((Map)JSON.parse(ret));
        }
        model.addAttribute("hotestMinds",hotMinds);

        return "index";
    }

    @GetMapping(value = "search")
    public String search(Model model,@RequestParam(name = "keyword")String keyword){
        if(StringUtils.isEmpty(keyword)){
            //throw new Exception("搜索关键词不能为空");
            return null;
        }else{
            Set<Object> sets = mindService.scan(keyword);
            List<Map> listMap = new ArrayList<>();
            for (Object mid:sets) {
                listMap.add((Map)JSON.parse(mindService.get((String) mid)));
            }
            model.addAttribute("minds",listMap);
        }
        return "search";
    }

}
