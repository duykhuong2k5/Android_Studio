package com.example.pandora.model.response;

import com.example.pandora.enums.DayStepType;
import com.example.pandora.enums.GameOptionType;
import com.example.pandora.enums.JobType;

import java.util.List;

public class AiQuestionResponse {
    private Long templateId;
    private JobType jobType;
    private DayStepType stepType;
    private int level;
    private GameOptionType optionType;
    private String question;
    private List<String> options;

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public DayStepType getStepType() { return stepType; }
    public void setStepType(DayStepType stepType) { this.stepType = stepType; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public GameOptionType getOptionType() { return optionType; }
    public void setOptionType(GameOptionType optionType) { this.optionType = optionType; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}
