/* Licensed under Apache-2.0 */
package io.terrible.batch.thumbnails.utils;

import java.util.ArrayList;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommandUtils {

  /**
   * Lazy seek to the given timestamp and create a thumbnail of whatever is on the screen at that
   * point. Save the thumbnail to the output location given.
   */
  public ArrayList<String> createThumbnail(
      final String timestamp, final String input, final String output) {

    final ArrayList<String> commands = new ArrayList<>();

    commands.add("ffmpeg");
    commands.add("-y");
    commands.addAll(silentOperation());
    commands.add("-ss");
    commands.add(timestamp);
    commands.add("-i");
    commands.add(input);
    commands.add("-vframes");
    commands.add("1");
    commands.add(output);

    return commands;
  }

  /** Calculate the duration of a media file. */
  public ArrayList<String> calculateDuration(final String input) {

    final ArrayList<String> commands = new ArrayList<>();

    commands.add("ffprobe");
    commands.addAll(silentOperation());
    commands.add("-show_entries");
    commands.add("format=duration");
    commands.add("-of");
    commands.add("default=noprint_wrappers=1:nokey=1");
    commands.add(input);

    return commands;
  }

  /**
   * Hide output from FFMPEG. This will remove the banner, the output statistics, and anything that
   * isn't a panic level error.
   */
  private ArrayList<String> silentOperation() {

    final ArrayList<String> commands = new ArrayList<>();
    commands.add("-hide_banner");
    commands.add("-loglevel");
    commands.add("16");

    return commands;
  }
}
