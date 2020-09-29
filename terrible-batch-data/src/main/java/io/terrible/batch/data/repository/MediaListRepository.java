/* Licensed under Apache-2.0 */
package io.terrible.batch.data.repository;

import io.terrible.batch.data.domain.MediaList;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaListRepository extends MongoRepository<MediaList, String> {

  List<MediaList> findAllByName(String name);

  List<MediaList> findAllByNameIsNot(String filter);
}
