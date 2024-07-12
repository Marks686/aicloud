package org.spring.aicloud.controller;

import cn.hutool.core.lang.hash.Hash;
import cn.hutool.http.HttpRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import okhttp3.HttpUrl;
import org.spring.aicloud.entity.Answer;
import org.spring.aicloud.entity.enums.AiModelEnum;
import org.spring.aicloud.entity.enums.AiTypeEnum;
import org.spring.aicloud.service.IAnswerService;
import org.spring.aicloud.util.MinIoUtil;
import org.spring.aicloud.util.ResponseEntity;
import org.spring.aicloud.util.SecurityUtil;
import org.spring.aicloud.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

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



    @Value("${xunfei.draw.app-id}")
    private String appId;
    @Value("${xunfei.draw.host-url}")
    private String hostUrl;
    @Value("${xunfei.draw.api-key}")
    private String drawApiKey;
    @Value("${xunfei.draw.api-secret}")
    private String drawApiSecret;



    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private IAnswerService answerService;

    @Resource
    private MinIoUtil minIoUtil;

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


    /**
     * 调用讯飞模型历史对话信息
     */
    @RequestMapping("/getchatlist")
    public ResponseEntity getChatList(){
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",SecurityUtil.getCurrentUser().getUid());
        queryWrapper.eq("type",AiTypeEnum.CHAT.getValue());
        queryWrapper.eq("model",AiModelEnum.XUNFEI.getValue());
        queryWrapper.orderByDesc("aid");
        return ResponseEntity.succ(answerService.list(queryWrapper));
    }

    /**
     * 调用讯飞大模型绘画功能
     */
    @RequestMapping("/draw")
    public ResponseEntity draw(String question) throws Exception {
        if (!StringUtils.hasLength(question)) {
            return ResponseEntity.fail("问题不能为空");
        }
        String url = getAuthUrl(hostUrl, drawApiKey, drawApiSecret);
        String json = "{\n" +
                "  \"header\": {\n" +
                "    \"app_id\": \"" + appId + "\"\n" +
                "    },\n" +
                "  \"parameter\": {\n" +
                "    \"chat\": {\n" +
                "      \"domain\": \"general\",\n" +
                "      \"width\": 512,\n" +
                "      \"height\": 512\n" +
                "      }\n" +
                "    },\n" +
                "  \"payload\": {\n" +
                "    \"message\": {\n" +
                "      \"text\": [\n" +
                "        {\n" +
                "          \"role\": \"user\",\n" +
                "          \"content\": \"" + question + "\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        String result = HttpRequest.post(url)
                .body(json)
                .execute()
                .body();
        HashMap<String, Object> resultMap = objectMapper.readValue(result, HashMap.class);
        LinkedHashMap<String, Object> palyload = (LinkedHashMap<String, Object>)
                resultMap.get("payload");
        LinkedHashMap<String, Object> choices = (LinkedHashMap<String, Object>)
                palyload.get("choices");
        ArrayList<LinkedHashMap<String, Object>> text = (ArrayList<LinkedHashMap<String, Object>>)
                choices.get("text");
        LinkedHashMap<String, Object> contentMap = text.get(0);
        String content = contentMap.get("content").toString();
        // base64 -> MinIO
        String imgUrl = "";
        try (ByteArrayInputStream inputStream = new
                ByteArrayInputStream(Base64.getDecoder().decode(content))) {
            String imgName = "xf-draw-" + UUID.randomUUID().toString()
                    .replace("-", "");
            imgUrl = minIoUtil.upload(imgName, inputStream, "image/png");
        }
        Answer answer = new Answer();
        answer.setTitle(question);
        answer.setContent(imgUrl);
        answer.setModel(AiModelEnum.XUNFEI.getValue());
        answer.setUid(SecurityUtil.getCurrentUser().getUid());
        answer.setType(AiTypeEnum.DRAW.getValue());
        boolean saveResult = answerService.save(answer);
        if (saveResult){
            return ResponseEntity.succ(imgUrl);
        }
        return ResponseEntity.fail("操作失败，请重试！");
    }


    /**
     * 讯飞大模型 URL 签名方法
     *
     * @param hostUrl
     * @param apiKey
     * @param apiSecret
     * @return
     * @throws Exception
     */
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // date="Thu, 12 Oct 2023 03:05:28 GMT";
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" + "date: " + date + "\n" + "POST " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();
        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }
    /**
     * 获取所有绘图信息
     */
    @RequestMapping("/getdrawlist")
    public ResponseEntity getDrawList() {
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("model", AiModelEnum.XUNFEI.getValue());
        queryWrapper.eq("type", AiTypeEnum.DRAW.getValue());
        queryWrapper.eq("uid",SecurityUtil.getCurrentUser().getUid());
        queryWrapper.orderByDesc("aid");
        return ResponseEntity.succ(answerService.list(queryWrapper));
    }

}
