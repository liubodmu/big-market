package cn.bugstack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 前端 SPA 路由 fallback 配置。
 * <p>
 * 前端构建产物（big-market-web/dist）被拷贝到 classpath:/static/，由 Spring Boot
 * 默认静态资源处理直接提供。Vue Router 使用 history 模式，直接刷新 /raffle、/account
 * 等深层路径时后端会收到一个没有 Controller 匹配的请求，这里统一回落到 index.html，
 * 交给前端路由渲染，避免 404。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 前端路由顶层路径统一回落到 index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/raffle").setViewName("forward:/index.html");
        registry.addViewController("/account").setViewName("forward:/index.html");
        registry.addViewController("/exchange").setViewName("forward:/index.html");
        registry.addViewController("/records").setViewName("forward:/index.html");
        registry.addViewController("/admin/armory").setViewName("forward:/index.html");
        registry.addViewController("/admin/dcc").setViewName("forward:/index.html");
    }

}
