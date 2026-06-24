package com.dorr.aicode.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

//@Configuration
public class JsonConfig implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(tools.jackson.databind.json.JsonMapper.Builder builder) {
        SimpleModule module = new SimpleModule();
        // Long、long 转为字符串，解决前端精度丢失
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        builder.addModule(module);
    }
}