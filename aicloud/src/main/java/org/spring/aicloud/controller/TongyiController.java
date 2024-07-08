package org.spring.aicloud.controller;

import com.alibaba.cloud.ai.tongyi.chat.TongYiChatClient;
import com.alibaba.cloud.ai.tongyi.chat.TongYiChatOptions;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesClient;
import com.alibaba.cloud.ai.tongyi.image.TongYiImagesOptions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
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

import java.util.List;

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
                .setType(AiTypeEnum.CHAT.getValue())  // todo: 后面改成枚举
                .setModel(AiModelEnum.TONGYI.getValue())  // todo: 后面改成枚举
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
    /**
     * 绘画
     *
     * @param question
     * @return
     */
    @RequestMapping("/draw")
    public ResponseEntity draw(String question) {
        if (!StringUtils.hasLength(question)) {
            return ResponseEntity.fail("问题不能为空");
        }
        String result = imageClient.call(new ImagePrompt(question))
                .getResult().getOutput().getUrl();
        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(result);
        answer.setModel(AiModelEnum.TONGYI.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        answer.setType(AiTypeEnum.DRAW.getValue());
        boolean saveResult = answerService.save(answer);
        if (saveResult) {
            return ResponseEntity.succ(result);
        }
        return ResponseEntity.fail("操作失败，请重试！");
    }


    /**
     * 获取聊天列表
     */
    @RequestMapping("/getchatlist")
    public ResponseEntity getChatList() {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", SecurityUtil.getCurrentUser().getUid());
        queryWrapper.eq("type", AiTypeEnum.CHAT.getValue());
        queryWrapper.eq("model", AiModelEnum.TONGYI.getValue());
        queryWrapper.orderByDesc("aid");
        List<Answer> list = answerService.list(queryWrapper);
        return ResponseEntity.succ(list);
    }

    /**
     * 获取绘画历史列表
     */
    @RequestMapping("/getdrawlist")
    public ResponseEntity getDrawList() {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model",AiModelEnum.TONGYI.getValue()); //
        queryWrapper.eq("type",AiTypeEnum.DRAW.getValue()); //
        queryWrapper.eq("uid",SecurityUtil.getCurrentUser().getUid());
        queryWrapper.orderByDesc("aid");
        return ResponseEntity.succ(answerService.list(queryWrapper));
    }
}
