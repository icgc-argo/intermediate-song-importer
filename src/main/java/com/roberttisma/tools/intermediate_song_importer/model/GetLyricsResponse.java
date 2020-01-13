package com.roberttisma.tools.intermediate_song_importer.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetLyricsResponse {
  private String term;
  private List<Lyric> result;
}
