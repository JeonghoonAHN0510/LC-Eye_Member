package lceye.model.dto;

import lceye.model.entity.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    // 1. 기본적인 정보
    private int pjno;
    private String pjname;
    private double pjamount;
    private String pjdesc;
    private String pjfilename;
    private int mno;
    private int uno;
    private String createdate;
    private String updatedate;


    // 2. 부가적인 정보
    private String mname;

    // 3. toEntity 생성
    public ProjectEntity toEntity(){
        return ProjectEntity.builder()
                .pjno(this.pjno)
                .pjname(this.pjname)
                .pjamount(this.pjamount)
                .pjdesc(this.pjdesc)
                .pjfilename(this.pjfilename)
                .build();
    } // func end
} // class end