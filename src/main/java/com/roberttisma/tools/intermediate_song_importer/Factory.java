package com.roberttisma.tools.intermediate_song_importer;

import static com.roberttisma.tools.intermediate_song_importer.web.CachingLyricClient.createFileCachingLyricClient;

import com.roberttisma.tools.intermediate_song_importer.web.LyricClient;
import com.roberttisma.tools.intermediate_song_importer.web.UnirestLyricClient;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

public class Factory {

  @SneakyThrows
  public static LyricClient buildLyricClient(int uid, @NonNull String tokenid) {
    val internalClient = UnirestLyricClient.builder().tokenId(tokenid).uid(uid).build();
    return createFileCachingLyricClient("./", internalClient);
  }
}
