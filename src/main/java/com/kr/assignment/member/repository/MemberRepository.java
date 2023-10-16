package com.kr.assignment.member.repository;

import com.kr.assignment.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {

     Member findByEmail(String email) throws Exception;

    Member findByName(String name);
}
