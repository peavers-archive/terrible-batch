/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.services;

import io.terrible.batch.data.domain.MediaFile;

import java.io.IOException;
import java.util.ArrayList;

public interface ScanService {

  ArrayList<MediaFile> scanVideos(String directory);
}
