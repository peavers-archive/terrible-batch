/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.utils;

import io.terrible.batch.data.domain.MediaFile;
import java.io.File;
import java.nio.file.Files;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class FileUtils {

  /** System key value for home directory. */
  private final String USER_HOME_PROPERTY = "user.home";

  /**
   * Location on the filesystem relative to the home directory to store thumbnails, if not provided
   * when setting the directory.
   */
  private final String THUMBNAIL_DIRECTORY = ".terrible/_thumbnails/";

  public String getThumbnailDirectory(final MediaFile mediaFile) {

    final File file =
        new File(System.getProperty(USER_HOME_PROPERTY), THUMBNAIL_DIRECTORY + mediaFile.getId());

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
