package com.example.aicloud.controller;

import com.example.aicloud.util.ResponseEntity;
import jakarta.annotation.Resource;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openai")
public class OpenAIController {
    @Resource
    private OpenAiChatModel chatModel;
    @Resource
    private OpenAiImageModel imageModel;
    /**
     * 调用OpenAI 聊天接口
     * @param question
     * @return
     */
    @RequestMapping("/chat")
    public ResponseEntity chat(String question) {
        if (!StringUtils.hasLength(question)){
            // 输入为空
            return ResponseEntity.fail("问题不能为空");
        }
        // 调用OpenAI 接口
        String result = chatModel.call(question);
        return ResponseEntity.succ(result);
    }

    @RequestMapping("/draw")
    public ResponseEntity draw(String question) {
        if (!StringUtils.hasLength(question)){
            // 输入为空
            return ResponseEntity.fail("问题不能为空");
        }
        // 调用OpenAI 接口
        ImageResponse result = imageModel.call(new ImagePrompt(question));
        return ResponseEntity.succ(result.getResults().get(0));
    }
}
