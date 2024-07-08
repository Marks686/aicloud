package org.spring.aicloud.controller;

import cn.hutool.http.HttpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 讯飞大模型
 */
@RestController
@RequestMapping("/xunfei")
public class XunfeiController {

    @Value("${xunfei.chat.url}")
    private String chatUrl;
    @Value("${xunfei.chat.api-key}")
    private String chatApiKey;
    @Value("${xunfei.chat.api-secret}")
    private String chatApiSecret;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private IAnswerService answerService;

    /**
     * 调用对话功能
     */
    @RequestMapping("/chat")
    public ResponseEntity chat(String question) throws JsonProcessingException {
        if (!StringUtils.hasLength(question)) {
            return ResponseEntity.fail("问题不能为空");
        }
        String bodyJson = "{\n" +
                "    \"model\":\"generalv3.5\",\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"" + question + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        String result = HttpRequest.post(chatUrl)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + chatApiKey + ":" + chatApiSecret)
                .body(bodyJson)
                .execute()
                .body();
        HashMap<String, Object> resultMap = objectMapper.readValue(result, HashMap.class);
        if (!resultMap.get("code").toString().equals("0")) {
            return ResponseEntity.fail(resultMap.get("message").toString());
        }
        ArrayList choices = (ArrayList) resultMap.get("choices");
        LinkedHashMap<String,Object> choicesMap = (LinkedHashMap)choices.get(0);
        LinkedHashMap<String,Object> message = (LinkedHashMap<String, Object>) choicesMap.get("message");
        String content = message.get("content").toString();
        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(content);
        answer.setModel(AiModelEnum.XUNFEI.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        answer.setType(AiTypeEnum.CHAT.getValue());
        boolean saveResutl = answerService.save(answer);
        if(saveResutl){
            return ResponseEntity.succ(content);
        }
        return ResponseEntity.fail("操作失败，请重试！");
    }
}
