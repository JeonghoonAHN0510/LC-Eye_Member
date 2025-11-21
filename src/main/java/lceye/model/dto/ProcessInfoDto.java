package lceye.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInfoDto {
    // 1. 기본적인 정보
    private int pcno;
    private String pcuuid;
    private String pcname;
    private String pcdesc;
    private String pcsource;
//    private String pcfilename; // OngTK 비활성화
    private String createdate;
    private String updatedate;

    // 2. 부가적인 정보

} // class end