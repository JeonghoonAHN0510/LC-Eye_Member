package lceye.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lceye.service.LCICalculateService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lci")
@RequiredArgsConstructor
public class LCICalculateController {

    private final LCICalculateService lciCalculateService;

    /**
     * [LCI-01] LCI 계산하기
     * <p>
     * 관련 로직은 노션 참고
     * <p>
     * <a href="https://lceye.notion.site/2-6-LCI-2ae094d4983480e0a2efdb327c9e5359?source=copy_link">notion</a>
     * @author OngTK
     */
    @GetMapping("/calc")
    public ResponseEntity<?> calcLCI(@RequestParam int pjno){
        // todo OngTK calcLCI 구현 필요
        return ResponseEntity.ok(lciCalculateService.calcLCI(pjno));
    } // func end

    /**
     * [LCI-02] LCI 결과 조회하기
     * @author OngTK
     */
    @GetMapping
    public ResponseEntity<?> readLCI(@RequestParam int pjno){
        return ResponseEntity.ok(lciCalculateService.readLCI(pjno));
    } // func end

} // class end
