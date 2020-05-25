/* Licensed under Apache-2.0 */
package io.terrible.batch.repository;

import io.terrible.batch.domain.MediaFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaFileRepository extends MongoRepository<MediaFile, String> {}
