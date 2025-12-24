package com.home.trip.service;

import com.home.trip.domain.Question;
import com.home.trip.domain.dto.question.QuestionDto;
import com.home.trip.domain.dto.question.QuestionOrderDto;
import com.home.trip.domain.dto.question.QuestionOrderUpdateDto;
import com.home.trip.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    /**
     * questions 컬렉션에서 Question 리스트를 order 오름차순으로 정렬해 반환합니다.
     * @return Question 리스트
     */
    public List<Question> findAllQuestions() {
        return questionRepository.findAll(Sort.by(Sort.Direction.ASC, "order"));
    }

    /**
     * questions 컬렉션에서 count()로 index를 추출해 insert()
     * @param questionDto 클라이언트에서 폼으로 요청
     */
    public void insertQuestion(QuestionDto questionDto) {
        Long count = questionRepository.count();
        questionDto.setOrder(count);
        Question question = Question.createQuestion(questionDto);
        questionRepository.insert(question);
    }

    /**
     * questions 컬렉션에서 id(question의 _id)로 질문을 찾아서 삭제
     * @param questionId 삭제할 id(question의 _id)
     * @throws IllegalArgumentException 해당 id(question의 _id)가 없을 때
     */
    public void deleteQuestion(String questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalArgumentException("해당 질문이 없습니다.");
        }
        questionRepository.deleteById(questionId);
    }

    /**
     * questions 컬렉션에서 id(question의 _id)로 질문을 찾아서 수정
     * @param questionId 삭제할 id(question의 _id)
     * @param questionDto 클라이언트에서 폼으로 요청
     * @throws IllegalArgumentException 해당 id(question의 _id)가 없을 때
     */
    public void updateQuestion(String questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));
        Question changeQuestion = question.changeQuestionOrOption(questionId, questionDto);
        questionRepository.save(changeQuestion);
    }

    /**
     * questions 컬렉션에서 id(question의 _id)로 질문을 찾아서 order를 수정
     * @param request questions 컬렉션의 모든 질문들 리스트
     * TODO: 성능 개선 필요
     */
    public void updateQuestionOrder(QuestionOrderUpdateDto request) {
        // 1. id 목록 추출
        List<String> ids = request.getQuestions().stream()
                .map(QuestionOrderDto::getId)
                .toList();

        // 2. 기존 질문 조회
        List<Question> questions = questionRepository.findAllById(ids);

        // 3. id → order 매핑
        Map<String, Long> orderMap = request.getQuestions().stream()
                .collect(Collectors.toMap(
                        QuestionOrderDto::getId,
                        QuestionOrderDto::getOrder
                ));

        // 4. order만 수정
        questions.forEach(q -> {
            Long newOrder = orderMap.get(q.getId()); // 해당 Id는 수정된 order가 저장
            q.changeOrder(newOrder); // 수정된 order로 변경
        });

        questionRepository.saveAll(questions);
    }


}
