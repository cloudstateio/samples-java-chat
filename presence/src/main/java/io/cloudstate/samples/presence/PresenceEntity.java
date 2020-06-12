package io.cloudstate.samples.presence;

import java.util.Optional;
import io.cloudstate.javasupport.crdt.*;
import cloudstate.samples.chat.presence.grpc.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Presence service is a CRDT entity that uses Vote to manage user online presence. */
@CrdtEntity
public class PresenceEntity {
  private static final Logger logger = LogManager.getLogger(PresenceEntity.class);

  private Vote presence = null;
  private int users = 0;

  public PresenceEntity(Vote presence) {
    this.presence = presence;
    logger.debug("Created entity");
  }

  /**
   * User presence monitoring call.
   *
   * This is a streamed call. We add a onStateChange callback, so that whenever the CRDT
   * changes, if the online status has changed since the last message we pushed, we push
   * it.
   */
  @CommandHandler OnlineStatus monitor(User user, StreamedCommandContext<OnlineStatus> ctx) {
    // Note we store the online status in an array for each call to monitor
    // even though it is a single boolean, as this lets us capture the object
    // in onChange callback's environment below.
    boolean onlineStatus[] = { presence.isAtLeastOne() };

    if(ctx.isStreamed()) {
      ctx.onChange(subCtx ->
       {
          boolean previousOnlineStatus = onlineStatus[0];
          boolean newOnlineStatus = presence.isAtLeastOne();
          onlineStatus[0] = newOnlineStatus;

          if(newOnlineStatus != previousOnlineStatus) {
            logger.debug("monitor: " + user.getName() + " return {" + newOnlineStatus + "}");
            return Optional.of(OnlineStatus.newBuilder().setOnline(newOnlineStatus).build());
          } else {
            logger.debug("monitor: " + user.getName() + " status unchanged");
            return Optional.empty();
          }
       });
    }

    logger.debug("monitor: " + user.getName() + " return {" + onlineStatus + "}");
    return OnlineStatus.newBuilder().setOnline(onlineStatus[0]).build();
  }

   /**
   * Connect a user, to make their presence active.
   *
   * This is a streamed call. As long as a user (id given by the entity id) is connected
   * to it, they are considered to be online.
   *
   * Here we use a Vote CRDT, which if at least one node votes is true, will be true.
   * So when the user connects, we invoke the connect() method (which we have defined
   * by enriching the CRDT in onStateSet), which will manage our vote accordingly.
   *
   * When they disconnect, the onStreamCancel callback is invoked, and we disconnect,
   * removing our vote if this is the last connection to this CRDT.
   */
  @CommandHandler
  public Empty connect(StreamedCommandContext<Empty> ctx) {

    if(ctx.isStreamed()) {

      ctx.onChange(s -> {
        logger.debug("connect: change!");
        return Optional.empty();
      });

      ctx.onCancel(a -> {
        users -= 1;
        if (users == 0) {
          presence.vote(false);
        }

        logger.debug("connect: cancelled stream. users = {}", users);
      });

      users += 1;
      if (users ==1) {
        presence.vote(true);
      }

      logger.debug("users = {}", users);

    }
    else {
      logger.debug("not streamed");
      ctx.fail("Call to connect must be streamed");
    }
    return Empty.getDefaultInstance();
  }

}
