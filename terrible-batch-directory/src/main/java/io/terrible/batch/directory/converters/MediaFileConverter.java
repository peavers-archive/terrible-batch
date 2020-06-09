/* Licensed under Apache-2.0 */
package io.terrible.batch.directory.converters;

import com.google.common.hash.Hashing;
import io.terrible.batch.data.domain.Directory;
import io.terrible.batch.data.domain.MediaFile;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

/** Converts a file to a type of MediaFile */
@Slf4j
@UtilityClass
public class MediaFileConverter {

  public MediaFile convert(final File file, final Directory directory) {

    return MediaFile.builder()
        .id(getId(file))
        .name(getName(file))
        .extension(getExtension(file))
        .path(getPath(file, directory))
        .virtualPath(file.getAbsolutePath())
        .size(getSize(file))
        .createdTime(getCreationTime(file))
        .createdTime(getCreationTime(file))
        .lastAccessTime(getLastAccessTime(file))
        .lastModifiedTime(getModificationTime(file))
        .build();
  }

  private LocalDateTime getCreationTime(final File file) {

    LocalDateTime time;
    try {
      final Path path = Paths.get(file.getAbsolutePath());
      final BasicFileAttributes fileAttributes =
          Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
      time =
          LocalDateTime.ofInstant(
              fileAttributes.creationTime().toInstant(), ZoneId.systemDefault());
    } catch (final Exception e) {
      time = null;
    }
    return time;
  }

  private LocalDateTime getModificationTime(final File file) {

    LocalDateTime time;
    try {
      final Path path = Paths.get(file.getAbsolutePath());
      final BasicFileAttributes fileAttributes =
          Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
      time =
          LocalDateTime.ofInstant(
              fileAttributes.lastModifiedTime().toInstant(), ZoneId.systemDefault());
    } catch (final Exception e) {
      time = null;
    }
    return time;
  }

  private LocalDateTime getLastAccessTime(final File file) {

    LocalDateTime time;
    try {
      final Path path = Paths.get(file.getAbsolutePath());
      final BasicFileAttributes fileAttributes =
          Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
      time =
          LocalDateTime.ofInstant(
              fileAttributes.lastAccessTime().toInstant(), ZoneId.systemDefault());
    } catch (final Exception e) {
      time = null;
    }
    return time;
  }

  private long getSize(final File file) {

    long size;
    try {
      final Path path = Paths.get(file.getAbsolutePath());
      final BasicFileAttributes fileAttributes =
          Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
      size = fileAttributes.size();
    } catch (final Exception e) {
      size = -1L;
    }
    return size;
  }

  private String getName(final File file) {

    return FilenameUtils.removeExtension(file.getName());
  }

  private String getExtension(final File file) {

    return FilenameUtils.getExtension(file.getName());
  }

  private String getId(final File file) {
    try {
      //noinspection UnstableApiUsage
      return Hashing.murmur3_128()
          .hashString(file.getCanonicalPath(), StandardCharsets.UTF_8)
          .toString();
    } catch (final IOException e) {
      return null;
    }
  }

  private String getPath(final File file, final Directory directory) {

    final String virtualPath = "/terrible/terrible-media/";

    return file.getAbsolutePath().contains(virtualPath)
        ? file.getAbsolutePath().replace(virtualPath, directory.getPath())
        : file.getAbsolutePath();
  }
}
