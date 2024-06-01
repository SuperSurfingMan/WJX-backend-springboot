package com.example.backend.service;

import com.example.backend.DTO.*;
import com.example.backend.entity.Question;
import com.example.backend.entity.Survey;
import com.example.backend.entity.SurveyState;
import com.example.backend.mapper.QuestionMapper;
import com.example.backend.mapper.ResponseMapper;
import com.example.backend.mapper.SurveyMapper;
import com.example.backend.mapper.SurveyStateMapper;
import com.example.backend.service.serviceInterface.SurveyServiceInter;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurveyServiceImpl implements SurveyServiceInter {

    private final SurveyMapper surveyMapper;
    private final QuestionMapper questionMapper;
    private final ResponseMapper responseMapper;
    private final SurveyStateMapper surveyStateMapper; // 新增


    public SurveyServiceImpl(SurveyMapper surveyMapper, QuestionMapper questionMapper, ResponseMapper responseMapper, SurveyStateMapper surveyStateMapper) {
        this.surveyMapper = surveyMapper;
        this.questionMapper = questionMapper;
        this.responseMapper = responseMapper;
        this.surveyStateMapper = surveyStateMapper;
    }


    private Survey convertToEntity(SurveyDto surveyDto) {
        Survey survey = new Survey();
        survey.setId(surveyDto.getId());
        survey.setTitle(surveyDto.getTitle());
        survey.setCreatedBy(surveyDto.getCreatedBy());
        survey.setDescription(surveyDto.getDescription());
        survey.setCreatedAt(surveyDto.getCreatedAt());
        return survey;
    }

    private SurveyDto convertSurveyToDto(Survey survey) {
        // 假设 Survey 有 getId(), getTitle(), getCreatedBy(), getDescription() 方法
        return new SurveyDto(survey.getId(), survey.getTitle(), survey.getCreatedBy(), survey.getDescription(), survey.getCreatedAt());
    }

    private QuestionDto convertQuestionToDto(Question question) {
        // 假设 Question 有相应的方法
        return new QuestionDto(question.getId(), question.getText(), question.getType(), question.getSurveyId());
    }


    @Override
    public SurveyDto createSurvey(SurveyDto surveyDto) {
        Survey survey = convertToEntity(surveyDto);
        surveyMapper.insert(survey);
        return convertSurveyToDto(survey);
    }

    @Override
    public List<SurveyDto> getAllSurveys() {
        return surveyMapper.selectAll().stream().map(this::convertSurveyToDto).collect(Collectors.toList());
    }


    @Override
    public SurveyDto getSurveyById(Long id) {
        Survey survey = surveyMapper.selectByPrimaryKey(id);
        return convertSurveyToDto(survey);
    }

    @Override
    public SurveyDto updateSurvey(Long id, SurveyDto surveyDto) {
        Survey survey = convertToEntity(surveyDto);
        survey.setId(id); // Ensure ID is set correctly
        surveyMapper.updateByPrimaryKey(survey);
        return convertSurveyToDto(survey);
    }

    @Override
    public void deleteSurvey(Long id) {
        surveyMapper.deleteByPrimaryKey(id);
    }



    @Override
    public List<QuestionDto> getQuestionsForSurvey(Long surveyId) {
        List<Question> questions = questionMapper.selectBySurveyId(surveyId);
        return questions.stream().map(this::convertQuestionToDto).collect(Collectors.toList());
    }

    //未实现
    @Override
    public List<OptionDto> getOptionsForQuestion(Long questionId) {


        return null;
    }


    public List<SurveyDto> getSurveysByUserId(Long userId) {
        List<Survey> surveys = surveyMapper.selectByUserId(userId);
        return surveys.stream().map(this::convertSurveyToDto).collect(Collectors.toList());
    }


    public List<SurveyDto> getSurveysByUserIdSorted(Long userId) {
        Sort sort = Sort.by(Sort.Order.asc("createdAt"));
        List<Survey> surveys = surveyMapper.selectByUserIdSorted(userId, sort);
        return surveys.stream().map(this::convertSurveyToDto).collect(Collectors.toList());

    }

    public List<QuestionResponse> getResponsesForSurvey(Long surveyId) {
        List<QuestionDto> questions = getQuestionsForSurvey(surveyId);
        List<QuestionResponse> allResponses = new ArrayList<>();

        ResponseServiceImpl responseService;
        responseService = new ResponseServiceImpl(responseMapper);
        for (QuestionDto question : questions) {
            List<ResponseDto> responses = responseService.getResponsesForQuestion(question.getId());

            System.out.println(responses);
            for(ResponseDto response : responses){
                if(response.getanswerText() == null){
                    System.out.println("Response is null");
                }

                System.out.println("Question: " + question.getText() + " Response: " + response.getanswerText());
                allResponses.add(new QuestionResponse(question.getText(), response.answerText()));
            }

        }




        return allResponses;
    }

    public SurveyStateDto getSurveyState(Long surveyId) {

       SurveyState surveyState=surveyStateMapper.selectSurveyStateBySurveyId(surveyId);


        return new SurveyStateDto(surveyState.getSurveyId(), surveyState.getId(), surveyState.getState());

    }

    public SurveyStateDto incrementSurveyState(Long surveyId) {
        SurveyState surveyState = surveyStateMapper.selectSurveyStateBySurveyId(surveyId);
        surveyState.setReceivenumber(surveyState.getReceivenumber() + 1);
        surveyStateMapper.updateByPrimaryKey(surveyState);
        return new SurveyStateDto(surveyState.getSurveyId(), surveyState.getReceivenumber(), surveyState.getState());
    }

    public SurveyStateDto changeSurveyState(SurveyStateDto surveyStateDto) {
        SurveyState surveyState = surveyStateMapper.selectSurveyStateBySurveyId(surveyStateDto.getSurveyId());
        surveyState.setState(surveyStateDto.getState());
        surveyStateMapper.updateByPrimaryKey(surveyState);
        return new SurveyStateDto(surveyState.getSurveyId(), surveyState.getReceivenumber(), surveyState.getState());
    }
}

