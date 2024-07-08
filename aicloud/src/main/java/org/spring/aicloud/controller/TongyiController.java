package org.spring.aicloud.controller;

import com.alibaba.cloud.ai.tongyi.chat.TongYiChatClient;
import com.alibaba.cloud.ai.tongyi.chat.TongYiChatOptions;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesClient;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesOptions;
import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Resource
    private IAnswerService answerService;

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

        Answer answer = new Answer();
        answer.setContent(result)
                .setTitle(question)
                .setType(1)  // todo: 后面改成枚举
                .setModel(2)  // todo: 后面改成枚举
                .setUid(SecurityUtil.getCurrentUser().getUid());

        boolean saveResult = answerService.save(answer);
        if (saveResult){
            return ResponseEntity.succ(result);
        }
        return ResponseEntity.fail("save answer fail");
    }

    /**
     * 绘画
     * @param question
     * @return
     */
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
