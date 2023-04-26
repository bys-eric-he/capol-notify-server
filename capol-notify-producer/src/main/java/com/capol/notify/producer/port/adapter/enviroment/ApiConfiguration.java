package com.capol.notify.producer.port.adapter.enviroment;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.Formatter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@EnableSwagger2
@Configuration
public class ApiConfiguration {

    private static final String AUTH_HEADER = "Authorization";

    @Bean
    public Docket producerApi() {
        return buildDocket("com.capol.notify.producer.port.adapter.restapi.controller")
                .groupName("消息服务生产者管理端API")
                .pathMapping("/")
                .apiInfo(new ApiInfoBuilder().title("消息服务生产者管理端API").version("V1.0").description("消息服务生产者管理端API").build());
    }

    private Docket buildDocket(String basePackage) {

        List<Parameter> parameters = new ArrayList<>();
        ParameterBuilder token = new ParameterBuilder();
        token.name(AUTH_HEADER)
                .description("Token串")
                .modelRef(new ModelRef("String"))
                .parameterType("header")
                .required(false).build();
        parameters.add(token.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(true)
                .directModelSubstitute(LocalTime.class, String.class)
                .directModelSubstitute(LocalDate.class, String.class)
                .directModelSubstitute(YearMonth.class, String.class)
                .directModelSubstitute(Duration.class, Long.class)
                .directModelSubstitute(BigDecimal.class, String.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(parameters);
    }

    /**
     * 取消API大小写敏感
     *
     * @return
     */
    @Bean
    public PathMatcher getPathMatcher() {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        return matcher;
    }

    @Bean
    public Jackson2ObjectMapperFactoryBean jackson2ObjectMapperFactoryBean() {
        Jackson2ObjectMapperFactoryBean jackson2ObjectMapperFactoryBean = new Jackson2ObjectMapperFactoryBean();
        jackson2ObjectMapperFactoryBean.setModules(Arrays.asList(
                new ParameterNamesModule(),
                new Jdk8Module(),
                new JavaTimeModule()
                /*new SimplePageJacksonModule()*/));
        jackson2ObjectMapperFactoryBean.setDeserializers(localDateTimeJsonDeserializer(),
                new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE),
                new LocalTimeDeserializer(DateTimeFormatter.ISO_LOCAL_TIME),
                new YearMonthDeserializer(DateTimeFormatter.ofPattern("yyyy-MM")));
        jackson2ObjectMapperFactoryBean.setSerializers(/*new PageJsonSerializer(),*/
                localDateTimeJsonSerializer(),
                new LocalDateSerializer(DateTimeFormatter.ISO_LOCAL_DATE),
                new LocalTimeSerializer(DateTimeFormatter.ISO_LOCAL_TIME),
                new YearMonthSerializer(DateTimeFormatter.ofPattern("yyyy-MM")));
        jackson2ObjectMapperFactoryBean.setFeaturesToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        jackson2ObjectMapperFactoryBean.setFailOnEmptyBeans(false);
        jackson2ObjectMapperFactoryBean.setSerializersByType(Map.of(BigDecimal.class, BigDecimalToStringSerializer.instance));
        return jackson2ObjectMapperFactoryBean;
    }

    /**
     * 增加如下配置可解决Spring Boot 6.x 与Swagger 3.0.0 不兼容问题
     **/
    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier,
                                                                         ServletEndpointsSupplier servletEndpointsSupplier,
                                                                         ControllerEndpointsSupplier controllerEndpointsSupplier,
                                                                         EndpointMediaTypes endpointMediaTypes,
                                                                         CorsEndpointProperties corsProperties,
                                                                         WebEndpointProperties webEndpointProperties,
                                                                         Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
    }

    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }

    private JsonDeserializer<LocalDateTime> localDateTimeJsonDeserializer() {
        return new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String txt = p.getText();
                if (txt == null || txt.trim().isEmpty()) {
                    return null;
                }
                try {
                    SimpleDateFormat text = new SimpleDateFormat();
                    StdDateFormat format = new StdDateFormat();
                    format = format.withLocale(Locale.CHINA);
                    format = format.withTimeZone(text.getTimeZone());
                    Date date = format.parse(txt.trim());
                    return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                } catch (Exception ex) {
                    throw new IOException("时间格式错误", ex);
                }
            }

            @Override
            public Class<LocalDateTime> handledType() {
                return LocalDateTime.class;
            }
        };
    }

    public JsonSerializer<LocalDateTime> localDateTimeJsonSerializer() {
        return new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                try {
                    ZoneId zone = ZoneId.systemDefault();
                    Instant instant = value.atZone(zone).toInstant();
                    Date date = Date.from(instant);
                    gen.writeString(new ObjectMapper().getDateFormat().format(date));
                } catch (Exception ex) {
                    gen.writeString("");
                }
            }

            @Override
            public Class<LocalDateTime> handledType() {
                return LocalDateTime.class;
            }
        };
    }

    /**
     * jackson 序列化对象
     *
     * @return
     */
    @Bean(name = "objectMapperBuilder")
    @ConditionalOnClass({ObjectMapper.class, Jackson2ObjectMapperBuilder.class})
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .modules(new ParameterNamesModule(),
                        new Jdk8Module(),
                        new JavaTimeModule()
                        /*new SimplePageJacksonModule()*/)
                .serializerByType(BigDecimal.class, ToStringSerializer.instance)
                .featuresToEnable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
    }

    @Bean
    public org.springframework.format.Formatter<LocalDate> localDateFormatter() {
        return new Formatter<LocalDate>() {
            @Override
            public LocalDate parse(String text, Locale locale) {
                return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
            }

            @Override
            public String print(LocalDate object, Locale locale) {
                return DateTimeFormatter.ISO_DATE.format(object);
            }
        };
    }

    @JacksonStdImpl
    static class BigDecimalToStringSerializer extends ToStringSerializer {
        public final static BigDecimalToStringSerializer instance = new BigDecimalToStringSerializer();

        public BigDecimalToStringSerializer() {
            super(Object.class);
        }

        public BigDecimalToStringSerializer(Class<?> handledType) {
            super(handledType);
        }

        @Override
        public boolean isEmpty(SerializerProvider prov, Object value) {
            if (value == null) {
                return true;
            }
            String str = ((BigDecimal) value).stripTrailingZeros().toPlainString();
            return str.isEmpty();
        }

        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeString(((BigDecimal) value).stripTrailingZeros().toPlainString());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return createSchemaNode("string", true);
        }

        @Override
        public void serializeWithType(Object value, JsonGenerator gen,
                                      SerializerProvider provider, TypeSerializer typeSer)
                throws IOException {
            // no type info, just regular serialization
            serialize(value, gen, provider);
        }
    }
}