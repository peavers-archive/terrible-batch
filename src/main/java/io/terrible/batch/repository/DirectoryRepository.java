/* Licensed under Apache-2.0 */
package io.terrible.batch.repository;

import io.terrible.batch.domain.Directory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryRepository extends MongoRepository<Directory, String> {}
