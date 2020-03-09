package com.roberttisma.tools.intermediate_song_importer.model.report;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

@Getter
public class FinalReport implements Report {

  private final Set<Report> success = newHashSet();
  private final Set<Report> errors = newHashSet();

  public void addReport(@NonNull Report r) {
    if (r.hasErrors()) {
      errors.add(r);
    } else {
      success.add(r);
    }
  }

  @Override
  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  public static FinalReport createFinalReport(@NonNull Collection<Report> reports) {
    val fr = new FinalReport();
    reports.forEach(fr::addReport);
    return fr;
  }
}
