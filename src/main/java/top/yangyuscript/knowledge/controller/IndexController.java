package top.yangyuscript.knowledge.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.yangyuscript.knowledge.common.ParamKey;
import top.yangyuscript.knowledge.service.MindService;
import top.yangyuscript.knowledge.service.UserService;
import top.yangyuscript.knowledge.utils.CusAccessObjectUtil;
import top.yangyuscript.knowledge.utils.EmailUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @RequestMapping(value = "detail",method = RequestMethod.POST)
    public String detail(Model model, @RequestParam(name = "mindString")String mindString, HttpServletRequest req){
        //记录脑图点击数
        Map<String,Object> mind = JSON.parseObject(mindString);
        String mid = (String)mind.get("mid");
        String ip = CusAccessObjectUtil.getIpAddress(req);
        mindService.addClick(mid,ip);

        model.addAttribute("mindString",mindString);
        return "detail";
    }

    @RequestMapping(value = "create",method = RequestMethod.GET)
    public String create(Model model,@RequestParam(name = "mid",required = false)String mid,@RequestParam(value = "email",required = false)String email,
                         @RequestParam(value = "token",required = false)String token) throws Exception{
        if(!StringUtils.isEmpty(mid)){
            String mind = mindService.get(mid);
            if(!StringUtils.isEmpty(mind)){
                model.addAttribute("mindStr",mind);
                model.addAttribute("mid",mid);
            }
        }
        return "create";
    }

    @RequestMapping(value = "saveMind",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> saveMind(@RequestParam(name = "mindString")String mindString,@RequestParam(name = "mid",required = false)String mid
            ,@RequestParam(name="email",required = false)String email){
        Map<String,Object> result = new HashMap<>();
        if(StringUtils.isEmpty(mid) || "mid".equals(mid)){
            result.put("mid",mindService.save(mindString,email));
        }else{
            mindService.update(mid,mindString);
            result.put("mid",mid);
        }
        result.put("result","ok");
        return result;
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

    @GetMapping(value = "myMindMaps")
    public String myMindeMaps(Model model,@RequestParam("email")String email,@RequestParam("token")String token){
        List<String> list = mindService.getUserMindMap(email);
        List<Map> listMap = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            list.stream().forEach(mid -> {
                listMap.add((Map)JSON.parse(mindService.get(mid)));
            });
        }
        model.addAttribute("minds",listMap);
        return "mine";
    }
}
