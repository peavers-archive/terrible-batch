/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.utils;

import io.terrible.batch.data.domain.MediaFile;
import java.io.File;
import java.nio.file.Files;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class FileUtils {

  public String getThumbnailDirectory(final String baseDir, final MediaFile mediaFile) {

    final File file = new File(baseDir + mediaFile.getId());

    return createDirectory(file);
  }

  /**
   * Create a new directory if possible on the host filesystem. The input path will be rejected if
   * permissions fail or the IO is unable to create. If the file directory already exists, the path
   * is returned and no IO operations are preformed.
   */
  private String createDirectory(File file) {

    if (Files.exists(file.toPath())) {
      return file.getAbsolutePath();
    }

    org.apache.commons.io.FileUtils.deleteQuietly(file);

    if (file.mkdirs()) {
      return file.getAbsolutePath();
    } else {
      log.warn("Unable to create thumbnail directory");
    }

    return file.getAbsolutePath();
  }
}
