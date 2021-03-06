package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.UserService;
import com.sun.deploy.net.cookie.CookieUnavailableException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String reg(Model model,
                      @RequestParam("username")String username,
                      @RequestParam("password")String password,
                      @RequestParam(value = "next",required = false)String next,
                      HttpServletResponse response) {
        try{
            Map<String,String> map=userService.register(username,password);
            if(map.containsKey("ticket")){

                Cookie cookie=new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if(StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }

        }catch (Exception e){
            logger.info("注册异常"+e.getMessage());
            return "login";
        }


    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model,
                      @RequestParam("username")String username,
                      @RequestParam("password")String password,
                        @RequestParam(value = "next",required = false)String next,
                        @RequestParam(value = "rememberme",defaultValue = "false")boolean rememberme,
                        HttpServletResponse response) {
        try{
            Map<String,String> map=userService.login(username,password);
            if(map.containsKey("ticket")){

                Cookie cookie=new Cookie("ticket",map.get("ticket"));
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);

                eventProducer.fireEvent(new EventModel(EventType.LOGIN)
                        .setExt("username", username).setExt("email", "2434992092@qq.com")
                        .setActorId(Integer.parseInt(map.get("userId"))));

                if(StringUtils.isNotBlank(next)){
                    return "redirect:"+next;
                }
                return "redirect:/";
            }else{
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.info("登录异常"+e.getMessage());
            return "login";
        }


    }

    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regloginPage(Model model, @RequestParam(value = "next", required = false) String next) {
       model.addAttribute("next", next);
        return "login";
    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/";
    }
}
