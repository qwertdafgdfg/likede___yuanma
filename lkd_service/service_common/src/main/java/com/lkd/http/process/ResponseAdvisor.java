//package com.lkd.http.process;
//
//import com.lkd.http.view.BaseResponse;
//import com.lkd.http.view.ExceptionResponse;
//import com.lkd.utils.JsonUtil;
//import org.springframework.core.MethodParameter;
//import org.springframework.core.io.FileUrlResource;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//@RestControllerAdvice(basePackages = "com.lkd")
//public class ResponseAdvisor implements ResponseBodyAdvice<Object>{
//    @Override
//    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
//        if(methodParameter.getExecutable().getName().contains("error")) return false;
//
//        return true;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
//
//        if(o instanceof BaseResponse){
//            return o;
//        }
//        if(o instanceof FileUrlResource){
//            return o;
//        }
//        if(o instanceof Boolean){
//            boolean result = (boolean)o;
//            return new BaseResponse<Boolean>(result);
//        }
//        if(o instanceof ExceptionResponse){
//            return new BaseResponse<>(400,((ExceptionResponse)o).getMsg());
//        }
//        if(o instanceof String){
//            BaseResponse<Object> result = new BaseResponse<>(o);
//
//            try {
//                String response = JsonUtil.serialize(result);
//
//                serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//                serverHttpResponse.getBody().write(response.getBytes());
//
//                return null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return new BaseResponse<>(o);
//    }
//}
