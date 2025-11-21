package lceye.model.dto;

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

} // class end