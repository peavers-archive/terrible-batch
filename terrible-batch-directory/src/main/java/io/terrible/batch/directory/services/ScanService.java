/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.services;

import io.terrible.batch.data.domain.MediaFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;

public interface ScanService {

  ArrayDeque<MediaFile> scanVideos(String directory) throws IOException;

  ArrayDeque<File> scanPictures(String directory) throws IOException;
}
