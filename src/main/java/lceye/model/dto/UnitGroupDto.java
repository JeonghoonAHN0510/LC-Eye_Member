package lceye.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitGroupDto {
    // 1. 기본적인 정보
    private int ugno;
    private String ugname;
    private String uguuid;
    private String createdate;
    private String updatedate;

    // 2. 부가적인 정보

} // class end