package com.toy.toyback.login.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestResponse {
    
    @JsonProperty("GcOutlawParking")
    private GcOutlawParking gcOutlawParking;

    @Data
    public static class GcOutlawParking {

        @JsonProperty("list_total_count")
        private int listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        private List<Row> row;
    }

    @Data
    public static class Result {

        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }

    @Data
    public static class Row {

        @JsonProperty("INST_NM")
        private String instNm;

        @JsonProperty("MDL_NM")
        private String mdlNm;

        @JsonProperty("SN")
        private String sn;

        @JsonProperty("ULFL_PKST_DTCT_CNT")
        private String ulflPkstDtctCnt;

        @JsonProperty("MNT5_OVER_PKST_VHCL_DTCT")
        private String mnt5OverPkstVhclDtct;

        @JsonProperty("OPER_YN")
        private String operYn;

        @JsonProperty("INPT_DT")
        private String inptDt;
    }

    
}
