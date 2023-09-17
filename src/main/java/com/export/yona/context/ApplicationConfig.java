//package com.export.yona.context;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.thymeleaf.ITemplateEngine;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.spring5.SpringTemplateEngine;
//import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
//import org.thymeleaf.spring5.view.ThymeleafViewResolver;
//import org.thymeleaf.templatemode.TemplateMode;
//import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
//import org.thymeleaf.templateresolver.ITemplateResolver;
//
////@EnableWebMvc
//@Configuration
//@ComponentScan(basePackages = "com.export.yona.*")
//public class ApplicationConfig implements WebMvcConfigurer {
//
//    @Bean
//    public ITemplateResolver templateResolver() {
//        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
//        templateResolver.setPrefix("templates/");
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode(TemplateMode.HTML);
//        return templateResolver;
//    }
//
//    @Bean
//    public ITemplateEngine templateEngine(ITemplateResolver templateResolver) {
//        SpringTemplateEngine engine = new SpringTemplateEngine();
//        engine.setEnableSpringELCompiler(true);
//        engine.setTemplateResolver(templateResolver);
//        return engine;
//    }
//
//}
