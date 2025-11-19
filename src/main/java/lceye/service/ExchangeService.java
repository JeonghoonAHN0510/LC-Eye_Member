package lceye.service;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import lceye.model.dto.ProcessInfoDto;
import lceye.model.dto.ProjectDto;
import lceye.model.dto.UnitsDto;
import lombok.RequiredArgsConstructor;

@Service @Transactional @RequiredArgsConstructor
public class ExchangeService {

    private final FileService fileService;
    private final JwtService jwtService;
    private final ProjectService projectService;
    private final TranslationService translationService;
    private final UnitsService unitsService;
    private final ProcessInfoService processInfoService;

    /**
     * 투입물·산출물 저장/수정
     *
     * @param exchangeList 투입물·산출물
     * @param token 작성자 토큰
     * @return boolean
     * @author 민성호
     */
    public boolean saveInfo(Map<String, Object> exchangeList , String token){
        if (!jwtService.validateToken(token)) return false;
        int cno = jwtService.getCnoFromClaims(token);
        int mno = jwtService.getMnoFromClaims(token);
        int pjNumber = (int) exchangeList.get("pjno");
        ProjectDto pjDto = projectService.findByPjno(pjNumber);
        if (pjDto == null) return false;
        UnitsDto unitsDto = unitsService.findByUno(pjDto.getUno());
        Object exchange = exchangeList.get("exchanges");
        if (exchange instanceof List<?> exList){
            for (Object inout : exList){
                if (inout instanceof Map io){
                    ProcessInfoDto pcDto = processInfoService.findByPcname(String.valueOf(io.get("pname")));
                    io.put("puuid",pcDto.getPcuuid());
                }// if end
            }// for end
        }// if end
        exchangeList.put("pjname",pjDto.getPjname());
        exchangeList.put("mno",mno);
        exchangeList.put("pjamount",pjDto.getPjamount());
        exchangeList.put("uno",pjDto.getUno());
        exchangeList.put("pjdesc",pjDto.getPjdesc());
        exchangeList.put("uname",unitsDto.getUnit());
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        // 파일 이름 형식 , cno_pjno_type_datetime(20251113_1600)
        String projectNumber = String.valueOf(exchangeList.get("pjno"));
        String name = cno + "_" + projectNumber + "_exchange_";
        String fileName;
        if(pjDto.getPjfilename() != null && !pjDto.getPjfilename().isEmpty()){ // json 파일명 존재할때
            Map<String,Object> oldJsonFile = fileService.readFile("exchange",pjDto.getPjfilename());
            exchangeList.put("createdate",oldJsonFile.get("createdate"));
            exchangeList.put("updatedate",now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            DateTimeFormatter change = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String createdate = String.valueOf(oldJsonFile.get("createdate"));
            LocalDateTime dateTime = LocalDateTime.parse(createdate,change);
            fileName = name + dateTime.format(formatter);
        }else {
            exchangeList.put("createdate",now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            fileName = name + now.format(formatter);
        }// if end
        boolean result = fileService.writeFile("exchange",fileName,exchangeList);
        if (result){
            boolean results = projectService.updatePjfilename(fileName,pjNumber);
            if (results) return true;
        }// if end
        return false;
    }// func end

    /**
     * 로그인한 회원의 회사파일에서 일치하는 데이터 찾기
     *
     * @param clientInput 클라이언트가 입력한 투입물·산출물
     * @param token 로그인한 회원의 토큰
     * @return Map<String,Object>
     * @author 민성호
     */
    public Map<String,Object> autoMatchCno(List<String> clientInput , String token){
        if (!jwtService.validateToken(token)) return null;
        int cno = jwtService.getCnoFromClaims(token);
        System.out.println("clientInput = " + clientInput + ", token = " + token);
        Set<String> inputSet = new HashSet<>(clientInput);
        Map<String, List<String>> requestMap = new HashMap<>();
        String companyNumber = String.valueOf(cno);
        List<Map<String, Object>> list = fileService.filterFile(companyNumber);
        for (Map<String, Object> map : list) {
            Object obj = map.get("exchanges");
            if (obj instanceof List<?> rawList) {
                for (Object exchangeObj : rawList) {
                    if (exchangeObj instanceof Map exchange) {
                        Object pjeNameObj = exchange.get("pjename");
                        Object pNameObj = exchange.get("pname");

                        // pjeName이 String 타입이고, Set에 포함되는지 확인
                        if (pjeNameObj instanceof String pjeName && inputSet.contains(pjeName)) {
                            String pName = pNameObj != null ? pNameObj.toString() : "N/A";
                            // 매칭된 결과를 해당 Key의 리스트에 추가 (덮어쓰기 방지)
                            requestMap.computeIfAbsent(pjeName, k -> new ArrayList<>()).add(pName);
                        }// if end
                    }// if end
                }// for end
            }// if end
        }// for end

        // 최종 반환 타입인 Map<String, Object>에 맞게 리턴
        System.out.println("requestMap: " + requestMap);
        return new HashMap<>(requestMap);
    }// func end

    /**
     * 로그인한 회원의 작성파일에서 일치하는 데이터 찾기
     *
     * @param clientInput 클라이언트가 입력한 투입물·산출물
     * @param token 로그인한 회원의 토큰
     * @return Map<String,Object>
     * @author 민성호
     */
    public Map<String,Object> autoMatchPjno(List<String> clientInput , String token){
        if (!jwtService.validateToken(token)) return null;
        int mno = jwtService.getMnoFromClaims(token);
        System.out.println("clientInput = " + clientInput + ", token = " + token);
        List<ProjectDto> projectDtos = projectService.findByMno(mno);
        Set<String> inputSet = new HashSet<>(clientInput);
        Map<String, List<String>> requestMap = new HashMap<>();
        List<Integer> pjnoList = projectDtos.stream().map(ProjectDto::getPjno).toList();

        for (int pjno : pjnoList) {
            List<Map<String, Object>> list = fileService.filterFile(String.valueOf(pjno));
            for (Map<String, Object> map : list) {
                Object obj = map.get("exchanges");
                if (obj instanceof List<?> rawList) {
                    for (Object exchangeObj : rawList) {
                        if (exchangeObj instanceof Map exchange) {
                            Object pjeNameObj = exchange.get("pjename");
                            Object pNameObj = exchange.get("pname");

                            // pjeName이 문자열이고, clientInput Set에 포함되는지 확인
                            if (pjeNameObj instanceof String pjeName && inputSet.contains(pjeName)) {
                                String pName = pNameObj != null ? pNameObj.toString() : "N/A";

                                // 매칭된 결과를 리스트에 추가 (덮어쓰기 방지)
                                requestMap.computeIfAbsent(pjeName, k -> new ArrayList<>()).add(pName);
                            }// if end
                        }// if end
                    }// for end
                }// if end
            }// for end
        }// for end

        // 최종 반환 타입에 맞게 Map<String, Object>로 반환
        System.out.println("requestMap: " + requestMap);
        return new HashMap<>(requestMap);
    }// func end

    /**
     * json 파일 삭제
     *
     * @param token 로그인한 회원의 토큰
     * @param pjno 삭제하는 프로젝트 번호
     * @return boolean
     * @author 민성호
     */
    public boolean clearIOInfo(String token , int pjno){
        if (!jwtService.validateToken(token)) return false;
        int cno = jwtService.getCnoFromClaims(token);
        ProjectDto dto = projectService.findByPjno(pjno);
        System.out.println("dto = " + dto);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
        if (dto != null){
            String projectNumber = String.valueOf(pjno);
            String name = cno + "_" + projectNumber + "_exchange_";
            DateTimeFormatter change = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(dto.getCreatedate(),change);
            String fileName = name + dateTime.format(formatter);
            boolean result = fileService.deleteFile(fileName,"exchange");
            System.out.println("result = " + result);
            if (result){
                boolean results = projectService.deletePjfilename(pjno);
                if (results) return true;
            }// if end
        }// if end
        return false;
    }// func end

    /**
     * 입력받은 투입물·산출물을 번역해서 db데이터와 유사도 측정
     *
     * @param clientInput 입력받은 투입물·산출물
     * @return Map<String,Object>
     * @author 민성호
     */
    public Map<String,Object> similarity(List<String> clientInput){
        List<String> transInput = translationService.TransInput(clientInput);
        System.out.println("transInput = " + transInput);
        List<ProcessInfoDto> processInfoEntities = processInfoService.getProcessInfo();
        JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
        final double benchmark = 0.90; // 90% 기준
        Map<String,List<String>> resultMatches = new HashMap<>();
        for (int i = 0; i < transInput.size(); i++){
            String client = clientInput.get(i);
            String input = transInput.get(i);
            List<String> matches = new ArrayList<>();
            if ("Routing".equals(input)) input = "diesel";
            String lowerInput = input.toLowerCase();
            for (ProcessInfoDto dto : processInfoEntities){
                String lowerPcdesc = dto.getPcdesc().toLowerCase();
                String lowerPcname = dto.getPcname().toLowerCase();
                Double scoreName = similarity.apply(lowerInput,lowerPcname);
                Double scoreDesc = similarity.apply(lowerInput,lowerPcdesc);
                boolean contains = lowerPcname.contains(lowerInput);
                boolean containsDesc = lowerPcdesc.contains(lowerInput);
                if (scoreName >= benchmark|| contains || scoreDesc >= benchmark || containsDesc){ // 유사도 90프로 이상
                    matches.add(dto.getPcname());
                }// if end
            }// for end
            if (!matches.isEmpty()){
                resultMatches.put(client,matches);
            }// if end
        }// for end
        System.out.println("resultMatches : " + resultMatches);
        return new HashMap<>(resultMatches);
    }// func end

}// class end
