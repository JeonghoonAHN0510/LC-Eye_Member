package lceye.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowDto {
    // 1. 기본적인 정보
    private int fno;
    private String fname;
    private String fuuid;
    private String casnumber;
    private int uno;
    private String createdate;
    private String updatedate;

    // 2. 부가적인 정보

} // class end