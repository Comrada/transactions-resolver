package com.github.comrada.crypto.wtc.resolver.parser.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StatusResponse(
    @JsonProperty
    String result,
    @JsonProperty("blockchain_count")
    int blockchainCount,
    @JsonProperty
    List<Blockchain> blockchains
) {

  public record Blockchain(
      @JsonProperty
      String name,
      @JsonProperty
      Set<String> symbols,
      @JsonProperty
      String status
  ) {}
}
