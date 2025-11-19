package lceye.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import lceye.model.dto.MemberDto;
import lceye.model.entity.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
} // interface end