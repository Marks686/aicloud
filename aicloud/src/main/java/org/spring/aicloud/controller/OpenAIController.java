package org.spring.aicloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.ResponseEntity;
import jakarta.annotation.Resource;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatClient;

import org.springframework.ai.openai.OpenAiImageClient;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openai")
public class OpenAIController {
    @Resource
    private OpenAiChatClient chatModel;
    @Resource
    private OpenAiImageClient imageModel;

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
        answer.setModel(AiModelEnum.OPENAI.getValue());
        answer.setType(AiTypeEnum.CHAT.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        boolean addResult = answerService.save(answer);
        if (addResult){
            return ResponseEntity.succ(result);
        }
        return ResponseEntity.fail("数据保存失败，请重试");
    }


    /**
     * 调用OpenAI 绘画接口
     * @param question
     * @return
     */
    @RequestMapping("/draw")
    public ResponseEntity draw(String question) {
        if (!StringUtils.hasLength(question)){
            // 输入为空
            return ResponseEntity.fail("问题不能为空");
        }
        // 调用OpenAI 接口
        ImageResponse result = imageModel.call(new ImagePrompt(question));
        String imgUrl = result.getResult().getOutput().getUrl();

        // 将结果保存到数据库
        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(imgUrl);
        answer.setModel(AiModelEnum.OPENAI.getValue());
        answer.setType(AiTypeEnum.DRAW.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        boolean addResult = answerService.save(answer);
        if (addResult){
            return ResponseEntity.succ(imgUrl);
        }
        return ResponseEntity.fail("数据保存失败，请重试");
    }


    /**
     * 获取openai聊天记录
     * @return
     */

    @RequestMapping("/getchatlist")
    public ResponseEntity getChatList() {
        Long uid = SecurityUtil.getCurrentUser().getUid();
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid);
        queryWrapper.eq("type",AiTypeEnum.CHAT.getValue());
        queryWrapper.eq("model",AiModelEnum.OPENAI.getValue());
        queryWrapper.orderByDesc("aid");

        return ResponseEntity.succ(answerService.list(queryWrapper));
    }

    /**
     * 获取openai绘画记录
     * @return
     */
    @RequestMapping("/getdrawlist")
    public ResponseEntity getDrawList() {
        Long uid = SecurityUtil.getCurrentUser().getUid();
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",uid);
        queryWrapper.eq("type",AiTypeEnum.DRAW.getValue());
        queryWrapper.eq("model",AiModelEnum.OPENAI.getValue());
        queryWrapper.orderByDesc("aid");

        return ResponseEntity.succ(answerService.list(queryWrapper));
    }

}
