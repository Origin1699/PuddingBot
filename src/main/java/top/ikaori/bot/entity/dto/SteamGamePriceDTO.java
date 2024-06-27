package top.ikaori.bot.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SteamGamePriceDTO {


    @JsonProperty("success")
    private Boolean success;

    @JsonProperty("data")
    private DataDTO data;

    @NoArgsConstructor
    @Data
    public static class DataDTO {
        @JsonProperty("price_overview")
        private PriceOverviewDTO priceOverview;

        @NoArgsConstructor
        @Data
        public static class PriceOverviewDTO {
            @JsonProperty("currency")
            private String currency;
            @JsonProperty("initial")
            private Integer initial;
            @JsonProperty("final")
            private Integer finalX;
            @JsonProperty("discount_percent")
            private Integer discountPercent;
            @JsonProperty("initial_formatted")
            private String initialFormatted;
            @JsonProperty("final_formatted")
            private String finalFormatted;
        }
    }
}
