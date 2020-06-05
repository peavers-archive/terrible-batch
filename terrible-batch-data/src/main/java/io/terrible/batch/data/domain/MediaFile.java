/* Licensed under Apache-2.0 */
package io.terrible.batch.data.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayDeque;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "media-files")
public class MediaFile {

  @Id private String id;

  private String name;

  private String path;

  private String thumbnailPath;

  private String extension;

  private long size;

  @Builder.Default private ArrayDeque<String> thumbnails = new ArrayDeque<>(12);

  private LocalDateTime createdTime;

  private LocalDateTime lastAccessTime;

  private LocalDateTime lastModifiedTime;

  private LocalDateTime lastWatched;

  private boolean isIndexed;

  private boolean isIgnored;
}
