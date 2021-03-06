package com.sgrain.boot.autoconfigure.returnvalue;

import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseHttpEntityMethodReturnValueHandler;
import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseHttpHeadersReturnValueHandler;
import com.sgrain.boot.autoconfigure.returnvalue.handler.ResponseMethodReturnValueHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.HttpEntityMethodProcessor;
import org.springframework.web.servlet.mvc.method.annotation.HttpHeadersReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 控制器返回值配置处理类
 * @Version: 1.0
 */
@Configuration
@EnableConfigurationProperties(ReturnValueProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.return-value", name = "enable", havingValue = "true", matchIfMissing = true)
public class ReturnValueAutoConfiguration implements InitializingBean {

    private RequestMappingHandlerAdapter handlerAdapter;

    public ReturnValueAutoConfiguration(RequestMappingHandlerAdapter handlerAdapter){
        this.handlerAdapter = handlerAdapter;
    }
    @Override
    public void afterPropertiesSet()  {

        List<HandlerMethodReturnValueHandler> list = handlerAdapter.getReturnValueHandlers();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<HandlerMethodReturnValueHandler> pList = new ArrayList<>();
        for (HandlerMethodReturnValueHandler valueHandler: list) {
            if (valueHandler instanceof RequestResponseBodyMethodProcessor) {
                ResponseMethodReturnValueHandler proxy = new ResponseMethodReturnValueHandler(valueHandler);
                pList.add(proxy);
            } else if(valueHandler instanceof HttpEntityMethodProcessor){
                ResponseHttpEntityMethodReturnValueHandler proxy = new ResponseHttpEntityMethodReturnValueHandler(valueHandler);
                pList.add(proxy);
            } else if(valueHandler instanceof HttpHeadersReturnValueHandler){
                ResponseHttpHeadersReturnValueHandler proxy = new ResponseHttpHeadersReturnValueHandler(valueHandler);
                pList.add(proxy);
            } else {
                pList.add(valueHandler);
            }
        }
        handlerAdapter.setReturnValueHandlers(pList);

    }
}
