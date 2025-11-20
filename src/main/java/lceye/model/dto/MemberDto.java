package lceye.model.dto;

import java.io.Serializable;

import lceye.model.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto implements Serializable {
    // 1. 기본적인 정보
    private int mno;
    private String mname;
    private int cno;
    private String mid;
    private String mpwd;
    private String mrole;
    private String memail;
    private String mphone;
    private String createdate;
    private String updatedate;

    // 2. 부가적인 정보
    private String token;
    private String cname;
    private CompanyDto companyDto;

    // 3. toEntity 생성
    public MemberEntity toEntity(){
        return MemberEntity.builder()
                .mno(this.mno)
                .mname(this.mname)
                .mid(this.mid)
                .mrole(this.mrole)
                .memail(this.memail)
                .mphone(this.mphone)
                .build();
    } // func end
} // class end