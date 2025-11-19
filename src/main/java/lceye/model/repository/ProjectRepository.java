package lceye.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import lceye.model.entity.MemberEntity;
import lceye.model.entity.ProjectEntity;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Integer> {

    /**
     * [PJ-02-01] 프로젝트 전체 조회
     * 검색 기준 : mno 기반 전체 조회
     */
    List<ProjectEntity> findByMemberEntity(MemberEntity memberEntity);

    /**
     * [PJ-02-02] 프로젝트 전체 조회
     * 검색 기준 : cno
     */
    @Query(value = "select p.* from project p join member m on p.mno = m.mno where m.cno= :cno;",nativeQuery = true)
    List<ProjectEntity> searchCno(int cno);

    @Query(value = "select * from project where mno = :mno",nativeQuery = true)
    List<ProjectEntity> findByMno(int mno);
} // interface end
