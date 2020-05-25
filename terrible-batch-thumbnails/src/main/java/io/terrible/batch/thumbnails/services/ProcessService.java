/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.services;

import java.io.IOException;
import java.util.List;

public interface ProcessService {

  String execute(List<String> command) throws IOException, InterruptedException;
}
