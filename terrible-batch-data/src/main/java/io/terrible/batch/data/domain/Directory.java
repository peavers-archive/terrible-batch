/* Licensed under Apache-2.0 */
package io.terrible.batch.data.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Directory {

  @Id private String id;

  private String path;
}
