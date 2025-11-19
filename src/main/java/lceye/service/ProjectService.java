package lceye.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import lceye.model.dto.ProjectDto;
import lceye.model.entity.MemberEntity;
import lceye.model.entity.ProjectEntity;
import lceye.model.entity.UnitsEntity;
import lceye.model.repository.MemberRepository;
import lceye.model.repository.ProjectRepository;
import lceye.model.repository.UnitsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class ProjectService {
    private final JwtService jwtService;
    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final UnitsRepository unitsRepository ;
    /**
     * [PJ-01] 프로젝트 등록
     */
    public ProjectDto saveProject(String token, ProjectDto projectDto){
        System.out.println("ProjectService.saveProject");
        System.out.println("token = " + token + ", projectDto = " + projectDto);

        // [1.1] Token이 없으면
        if(!jwtService.validateToken(token)) return projectDto; // PK 발급 안되고 종료
        // [1.2] 토큰이 존재한다면, 토큰에서 mno(작성자) 정보를 추출
        int mno = jwtService.getMnoFromClaims(token);
        // [1.3] 부가 entity _ MemberEntity, UnitsEntity 가져오기
        MemberEntity memberEntity = memberRepository.getReferenceById(mno);
        UnitsEntity unitsEntity = unitsRepository.getReferenceById(projectDto.getUno());
        // [1.4] dto > entity
        ProjectEntity projectEntity = projectDto.toEntity();
        projectEntity.setMemberEntity(memberEntity);
        projectEntity.setUnitsEntity(unitsEntity);
        // [1.4] entity 저장
        projectRepository.save(projectEntity);
        // [1.5] 결과 반환
        return projectEntity.toDto();
    } // func end

    /**
     * 회원번호로 프로젝트 조회
     *
     * @param mno 회원번호
     * @return List<ProjectDto>
     * @author 민성호
     */
    public List<ProjectDto> findByMno(int mno ){
        List<ProjectEntity> entities = projectRepository.findByMno(mno);
        List<ProjectDto> dtoList = entities.stream().map(ProjectEntity::toDto).toList();
        return dtoList;
    }// func end

    /**
     * 프로젝트 번호로 조회
     *
     * @param pjno 프로젝트 번호
     * @return ProjectDto
     * @author 민성호
     */
    public ProjectDto findByPjno(int pjno){
        Optional<ProjectEntity> optional = projectRepository.findById(pjno);
        if (optional.isPresent()){
            ProjectEntity entity = optional.get();
            return entity.toDto();
        }// if end
        return null;
    }// func end

    /**
     * 프로젝트파일명 추가
     *
     * @param fileName 프로젝트파일명
     * @param pjno 프로젝트 번호
     * @return boolean
     * @author 민성호
     */
    public boolean updatePjfilename(String fileName , int pjno){
        Optional<ProjectEntity> optional = projectRepository.findById(pjno);
        if (optional.isPresent()){
            ProjectEntity entity = optional.get();
            entity.setPjfilename(fileName);
            return true;
        }// if end
        return false;
    }// func end

    /**
     * 프로젝트 파일 삭제 시 파일명 삭제
     *
     * @param pjno 프로젝트 번호
     * @return boolean
     * @author 민성호
     */
    public boolean deletePjfilename(int pjno){
        Optional<ProjectEntity> optional = projectRepository.findById(pjno);
        if (optional.isPresent()){
            ProjectEntity entity = optional.get();
            entity.setPjfilename(null);
            return true;
        }// if end
        return false;
    }// func end

    /**
     * [PJ-02] 프로젝트 전체조회
     */
    public List<ProjectDto> readAllProject(String token){
        // [2.1] Token이 없으면 null로 반환
        if(!jwtService.validateToken(token)) return null;
        // [2.2] Token이 있으면, 토큰에서 로그인한 사용자 정보 추출
        int mno = jwtService.getMnoFromClaims(token);
        String mrole = jwtService.getRoleFromClaims(token);
        // [2.3] mno로 MemberRepository 조회
        MemberEntity memberEntity = memberRepository.getReferenceById(mno);
        // [2.4] mrole(역할)에 따른 서로 다른 조회 구현
        // [2.4.1] mrole = admin or manager : cno 기반 프로젝트 전체 조회
        if(mrole.equals("ADMIN") || mrole.equals("MANAGER")){
            return projectRepository.searchCno(memberEntity.getCompanyEntity().getCno()).stream().map(ProjectEntity :: toDto).toList();
        }
        // [2.4.2] mrole = worker : mno 기반 본인이 작성한 프로젝트만 조회
        if(mrole.equals("WORKER")){
            return projectRepository.findByMemberEntity(memberEntity).stream().map(ProjectEntity :: toDto).toList();
        }
        return null;
    } // func end

    /**
     * [PJ-03] 프로젝트 개별 조회
     * <p>
     * 권한을 확인하여 worker면, 본인의 프로젝트만 상세 조회 가능
     * 권한이 admin, manager 이면, 본인 프로젝트가 아닌 회사 프로젝트도 조회 가능
     */
    public ProjectDto readProject(String token, int pjNo){
        // [3.1] Token, pjNo가 없으면 null로 반환
        if(!jwtService.validateToken(token)) return null;
        if(pjNo == 0) return null;
        // [3.2] Token이 있으면, 토큰에서 로그인한 사용자 정보 추출
        int mno = jwtService.getMnoFromClaims(token);
        String mrole = jwtService.getRoleFromClaims(token);
        // [3.3] mno로 MemberRepository 조회
        MemberEntity memberEntity = memberRepository.getReferenceById(mno);
        // [3.4] mrole(역할)에 따른 서로 다른 조회 구현
        // [3.5] mrole = admin or manager
        if(mrole.equals("ADMIN") || mrole.equals("MANAGER")){
            // [3.5.1] project Entity 조회
             ProjectEntity result = projectRepository.getReferenceById(pjNo);
            // [3.5.2] 작성자의 cno 와 로그인한 계정의 cno가 일치하지 않으면 null
            if( result.getMemberEntity().getCompanyEntity().getCno() != memberEntity.getCompanyEntity().getCno()) return null;
            // [3.5.3] 일치하므로 결과 반환
            return result.toDto();
        }
        // [3.6] mrole = worker
        if(mrole.equals("WORKER")){
            // [3.6.1] 프로젝트 Entity 조회
            ProjectDto result = projectRepository.getReferenceById(pjNo).toDto();
            // [3.6.2] 조회 결과의 mno와 로그인 계정의 mno가 일치하지 않으므로 null
            if(result.getMno() != mno) return null; //
            // [3.6.3] 조회 결과의 mno와 로그인 계정의 mno가 일치하므로 결과 반환
            return result;
        }
        return null;
    } // func end

    /**
     * [PJ-04] 프로젝트 수정
     */
    public ProjectDto updateProject(String token, ProjectDto projectDto){
        // [4.1] Token, pjNo가 없으면 null로 반환
        if(!jwtService.validateToken(token)) return null;
        if(projectDto.getPjno() == 0) return null;
        // [4.2] Token이 있으면, 토큰에서 로그인한 사용자 정보 추출
        int mno = jwtService.getMnoFromClaims(token);
        // [4.3] projectEntity 조회
        Optional<ProjectEntity> optional = projectRepository.findById(projectDto.getPjno());
        // [4.4] projectEntity 가 존재하는 지 확인
        if(optional.isPresent()){
            // [4.5] projectEntity를 optinal에서 꺼냄
            ProjectEntity entity = optional.get();
            // [4.6] projectEntity의 작성자와 지금 요청자가 일치하는 지 확인
            if(entity.getMemberEntity().getMno() != mno) return null;
            // [4.7] 일치할 경우, entity setter 진행
            entity.setPjname(projectDto.getPjname());
            entity.setPjdesc(projectDto.getPjdesc());
            entity.setPjamount(projectDto.getPjamount());
            entity.setUnitsEntity(unitsRepository.findById(projectDto.getUno()).get());
            // [4.8] 결과 반환
            return entity.toDto();
        }
        return null;
    }// func end

} // class end
