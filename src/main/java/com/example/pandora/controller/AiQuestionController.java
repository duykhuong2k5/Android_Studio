package com.example.pandora.controller;

import com.example.pandora.api.ApiResponse;
import com.example.pandora.enums.DayStepType;
import com.example.pandora.enums.JobType;
import com.example.pandora.model.QuestionTemplate;
import com.example.pandora.model.response.AiQuestionResponse;
import com.example.pandora.service.AiQuestionGeneratorService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/ai")
public class AiQuestionController {

    private final AiQuestionGeneratorService ai;

    public AiQuestionController(AiQuestionGeneratorService ai) {
        this.ai = ai;
    }

    // GET /api/v1/ai/question?jobType=DOCTOR&stepType=WAKE_UP&level=1
    @GetMapping("/question")
    public ApiResponse<AiQuestionResponse> question(@RequestParam JobType jobType,
                                                    @RequestParam DayStepType stepType,
                                                    @RequestParam(defaultValue = "1") int level) {
        QuestionTemplate t = ai.pick(jobType, stepType, level);

        AiQuestionResponse res = new AiQuestionResponse();
        res.setTemplateId(t.getId());
        res.setJobType(t.getJobType());
        res.setStepType(t.getStepType());
        res.setLevel(t.getLevel());
        res.setOptionType(t.getOptionType());
        res.setQuestion(t.getQuestion());
        res.setOptions(Arrays.asList(AiQuestionGeneratorService.splitOptions(t.getOptions())));

        return ApiResponse.ok(res);
    }
}
