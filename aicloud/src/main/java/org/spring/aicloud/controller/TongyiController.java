package org.spring.aicloud.controller;

import com.alibaba.cloud.ai.tongyi.chat.TongYiChatClient;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesClient;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesOptions;
import jakarta.annotation.Resource;
import org.spring.aicloud.util.ResponseEntity;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tongyi")
public class TongyiController {

    @Resource
    private TongYiChatClient chatClient;

    @Resource
    private TongYiImagesClient imageClient;

    /**
     * 聊天
     * @param question
     * @return
     */
    @RequestMapping("/chat")
    public ResponseEntity chat(String question) {
        if (!StringUtils.hasLength(question)){
            return ResponseEntity.fail("question is null");
        }
        String result = chatClient.call(new Prompt(question))
                .getResult()
                .getOutput()
                .getContent();
        return ResponseEntity.succ(result);
    }

    @RequestMapping("/draw")
    public ResponseEntity draw(String question) {
        if (!StringUtils.hasLength(question)){
            return ResponseEntity.fail("question is null");
        }
        String  result = imageClient.call(new ImagePrompt(question,
                TongYiImagesOptions.builder()
                .withHeight(100)
                .withWidth(100).build()
        )).getResult().getOutput().getUrl();
        return ResponseEntity.succ(result);
    }
}
