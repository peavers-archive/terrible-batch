/* Licensed under Apache-2.0 */
package io.terrible.batch.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
@RequiredArgsConstructor
public class SearchIndexTasklet implements Tasklet {

  public static final String INDEX = "media-files";

  @Override
  public RepeatStatus execute(
      final StepContribution contribution, final ChunkContext chunkContext) {

    log.info("Search tasklet executed");

    return null;
  }
}
