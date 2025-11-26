package lceye.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import lceye.model.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
} // interface end