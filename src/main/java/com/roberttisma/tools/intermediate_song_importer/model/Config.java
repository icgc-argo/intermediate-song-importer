package com.roberttisma.tools.intermediate_song_importer.model;

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

  @Singular private List<ProfileConfig> profiles;
}
