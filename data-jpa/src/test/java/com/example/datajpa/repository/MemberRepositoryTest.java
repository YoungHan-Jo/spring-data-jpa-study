package com.example.datajpa.repository;

import com.example.datajpa.dto.MemberDto;
import com.example.datajpa.entity.Member;
import com.example.datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() throws Exception {
        System.out.println("memberRepository = " + memberRepository.getClass());
       //given
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> optionalMember = memberRepository.findById(savedMember.getId());
        Member findMember = optionalMember.get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

    }

    @Test
    public void basicCRUD() {

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        Long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(member1);
        memberRepository.delete(member2);

        Long delete = memberRepository.count();
        assertThat(delete).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndGreaterThen() throws Exception {
       //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //when & then
        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void queryMethod() throws Exception {
       //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
       //when & then

        List<Member> memberTop1 = memberRepository.findTop1By();

        assertThat(memberTop1.get(0).getUsername()).isEqualTo("member1");

        Long count = memberRepository.countAllBy();
        assertThat(count).isEqualTo(2);

    }

    @Test
    public void queryTest() throws Exception {
       //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

       //when & then

        List<Member> result = memberRepository.findUser("AAA", 20);
        assertThat(result.get(0)).isEqualTo(m2);

    }
    
    @Test
    public void queryDto() throws Exception {
       //given
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);


        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        m1.setTeam(teamA);
        m2.setTeam(teamA);

        memberRepository.save(m1);
        memberRepository.save(m2);

        //when & then

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() throws Exception {
       //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

       //when & then

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }

    }

    @Test
    public void returnType() throws Exception {
        //given
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        //when & then
        List<Member> list = memberRepository.findListByUsername("AAA");
        System.out.println("list = " + list.size());

        Member member = memberRepository.findMemberByUsername("AAA");
        System.out.println("member = " + member);

        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA");
        System.out.println("member = " + optionalMember.get());

        System.out.println("===============================================");

        List<Member> results = memberRepository.findListByUsername("asdfasdf");
        System.out.println("results = " + results.size());

        Member findMember = memberRepository.findMemberByUsername("asdfasdf");
        System.out.println("findMember = " + findMember);
    }


    @Test
    public void paging() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        // 0페이지 부터 시작
        // 0페이지에서 3개
        // username DESC로 정렬 (필요없으면 생략가능)
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));


        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        assertThat(content.size()).isEqualTo(3); // 내용 개수
        assertThat(page.getTotalElements()).isEqualTo(5); // 전체 개수
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 수
        assertThat(page.isFirst()).isTrue(); // 첫페이지 인지
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는지

    }

    @Test
    public void paging_slice() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        //when
        Slice<Member> page = memberRepository.findByAge(age, pageRequest);

        //then
        List<Member> content = page.getContent();
        for (Member member : content) {
            System.out.println("member = " + member);
        }

        assertThat(content.size()).isEqualTo(3);
//        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
//        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

    }

    





}