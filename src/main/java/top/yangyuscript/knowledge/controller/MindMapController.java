package top.yangyuscript.knowledge.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.yangyuscript.knowledge.service.MindService;
import top.yangyuscript.knowledge.utils.CusAccessObjectUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Desc:
 * @Author: lingx
 * @Date: 18:41 2020/11/7
 */
@Controller
public class MindMapController {

    @Autowired
    private MindService mindService;

    @RequestMapping(value = "saveMind",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> saveMind(@RequestParam(name = "mindString")String mindString,@RequestParam(name = "mid",required = false)String mid
            ,@RequestParam(name="email",required = false)String email,@RequestParam(name="tags",required = false)String tags){
        Map<String,Object> result = new HashMap<>();
        if(StringUtils.isEmpty(mid) || "mid".equals(mid)){
            result.put("mid",mindService.save(mindString,email,tags));
        }else{
            mindService.update(mid,mindString,tags);
            result.put("mid",mid);
        }
        result.put("result","ok");
        return result;
    }

    @RequestMapping(value = "create",method = RequestMethod.GET)
    public String create(Model model,@RequestParam(name = "mid",required = false)String mid,@RequestParam(value = "email",required = false)String email,
                         @RequestParam(value = "token",required = false)String token) throws Exception{
        if(!StringUtils.isEmpty(mid)){
            String mind = mindService.get(mid);
            if(!StringUtils.isEmpty(mind)){
                model.addAttribute("mindStr",mind);
                model.addAttribute("mid",mid);
                model.addAttribute("tags", (List)((Map)JSON.parse(mind)).get("tags"));
            }
        }
        return "create";
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

    @GetMapping(value = "myMindMaps")
    public String myMindeMaps(Model model, @RequestParam("email")String email
            , @RequestParam("token")String token, @RequestParam(name = "tag",required = false)String tag){
        List<String> list = mindService.getUserMindMap(email);
        List<Map> listMap = new ArrayList<>();
        if(!CollectionUtils.isEmpty(list)){
            list.stream().forEach(mid -> {
                listMap.add((Map) JSON.parse(mindService.get(mid)));
            });
        }
        model.addAttribute("minds",listMap);
        model.addAttribute("allSize",listMap.size());

        // 分类
        Map<String,Integer> mindTags = new HashMap<>();
        Map<String,List<Map>> mindMapList = new HashMap<>();
        listMap.stream().forEach(m -> {
            if(m.containsKey("tags")){
                List<String> tagsList = (List)m.get("tags");
                for (String s : tagsList) {
                    mindTags.compute(s, (k,v) -> v == null ? 1 : ++v);
                    mindMapList.compute(s, (k,v) -> {
                        if(v == null){
                            List<Map> tempList = new ArrayList<>();
                            tempList.add(m);
                            return tempList;
                        }else{
                            v.add(m);
                            return v;
                        }
                    });
                }
            }
        });

        model.addAttribute("mindTags",mindTags);

        // 按type来查询
        if(!StringUtils.isEmpty(tag)){
            model.addAttribute("minds", mindMapList.get(tag));
        }
        return "mine";
    }
}
