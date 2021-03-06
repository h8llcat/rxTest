package sapotero.rxtest.utils.queue.threads.producers;

import sapotero.rxtest.managers.menu.interfaces.Command;
import sapotero.rxtest.managers.menu.invokers.LocalExecutor;
import timber.log.Timber;

public class LocalCommandProducer implements Runnable, AutoCloseable {

  private final Command command;
  private final LocalExecutor localExecutor;
  private String TAG = this.getClass().getSimpleName();

  public LocalCommandProducer(Command command) {
    this.command = command;
    localExecutor = new LocalExecutor();
  }

  @Override
  public void run() {
    Timber.tag(TAG).i("start run");
    if (command != null) {
      localExecutor
        .setCommand( command )
        .execute();
    }
  }

  @Override
  public void close() throws Exception {
    Timber.tag(TAG).e( "close" );
  }

}