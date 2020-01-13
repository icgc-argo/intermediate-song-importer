package com.roberttisma.tools.intermediate_song_importer.web;

import com.roberttisma.tools.intermediate_song_importer.model.GetLyricsResponse;

public interface LyricClient {

  GetLyricsResponse get(String term);
}
