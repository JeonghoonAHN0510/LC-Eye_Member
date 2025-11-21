package lceye.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitsDto {
    // 1. 기본적인 정보
    private int uno;
    private String unit;
    private int ugno;
    private double uvalue;
    private String uuuid;
    private String createdate;
    private String updatedate;

    // 2. 부가적인 정보
    private String ugname;

} // class end