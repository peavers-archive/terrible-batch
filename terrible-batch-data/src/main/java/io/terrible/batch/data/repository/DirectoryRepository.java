/* Licensed under Apache-2.0 */
package io.terrible.batch.data.repository;

import io.terrible.batch.data.domain.Directory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectoryRepository extends MongoRepository<Directory, String> {}
