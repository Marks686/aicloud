package org.spring.aicloud.controller;

import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.ResponseEntity;
import jakarta.annotation.Resource;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
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

    @Resource
    private IAnswerService answerService;

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

        // 将结果保存到数据库
        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(result);
        answer.setModel(1);
        answer.setType(1);
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        boolean addResult = answerService.save(answer);
        if (addResult){
            return ResponseEntity.succ(result);
        }
        return ResponseEntity.fail("数据保存失败，请重试");
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
