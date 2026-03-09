package com.oop.library_management.repository;

import com.oop.library_management.model.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByMembershipNumber(String membershipNumber);

    Optional<Member> findByMembershipNumber(String membershipNumber);
}
