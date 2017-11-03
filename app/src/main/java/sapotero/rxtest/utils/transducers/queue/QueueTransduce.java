package sapotero.rxtest.utils.transducers.queue;

import java.util.List;

import sapotero.rxtest.utils.queue.models.CommandInfo;

public class QueueTransduce {


  public static List<CommandInfo> sort(List<CommandInfo> queue, Boolean sortByExecutedRemote, Boolean sortByValue) {
    return new QueueReader(queue)
      .reduce(
        sortByExecutedRemote ? new SortExecutedRemote(sortByValue) : new SortExecutedLocal(sortByValue)
      );
  }

  public static List<CommandInfo> sortByState(List<CommandInfo> queue, CommandInfo.STATE state) {
    return new QueueReader(queue).reduce( new SortByState(state) );
  }
}