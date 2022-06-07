package com.github.comrada.wa.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record NewAlertEvent(
    @JsonProperty
    long id,
    @JsonProperty
    String message,
    @JsonProperty
    String link,
    @JsonProperty("posted_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssXXX")
    ZonedDateTime postedAt,
    @JsonProperty
    String asset,
    @JsonProperty
    @JsonDeserialize(as = BigDecimal.class)
    BigDecimal amount
) {

}
