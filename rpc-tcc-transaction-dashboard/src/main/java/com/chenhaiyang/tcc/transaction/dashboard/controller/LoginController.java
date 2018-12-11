package com.chenhaiyang.tcc.transaction.dashboard.controller;

import com.chenhaiyang.tcc.transaction.dashboard.component.RootNodeRecorder;
import com.chenhaiyang.tcc.transaction.dashboard.component.vo.Tip;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登陆控制台
 * @author chenhaiyang
 */
@RestController
public class LoginController {
    /**
     * 登陆提醒
     */
    @Resource
    private RootNodeRecorder iRootNodeRecorder;

    /**
     * 登录跳转
     * @return 登录页
     */
    @GetMapping(value = "/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }
    /**
     * 首页输入提醒
     * @return tips
     */
    @RequestMapping("/tips")
    public @ResponseBody
    List<Tip> getTips(){
        List<String> tips = iRootNodeRecorder.listNode();

        return tips.stream()
                .map(tip->new Tip(tip,""))
                .collect(Collectors.toList());
    }
}
