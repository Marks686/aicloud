package org.spring.aicloud.util;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一返回对象 (进行 ajax 响应)
 */
@Data
public class ResponseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 4581593792717613764L;
    private int code; // 状态码
    private String msg; // 状态码描述信息
    private Object data; // 返回数据

    public static ResponseEntity succ(Object data){
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setCode(200);
        responseEntity.setMsg("成功");
        responseEntity.setData(data);
        return responseEntity;
    }

    public static ResponseEntity fail(String msg){
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setCode(500);
        responseEntity.setMsg(msg);
        responseEntity.setData(null);
        return responseEntity;
    }
}
