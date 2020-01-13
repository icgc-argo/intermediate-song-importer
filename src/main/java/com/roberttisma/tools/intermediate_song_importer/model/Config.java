package com.roberttisma.tools.intermediate_song_importer.model;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Config {

  @Singular private List<ProfileConfig> profiles = newArrayList();
}
