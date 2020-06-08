package io.cloudstate.samples.friends;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.cloudstate.javasupport.crdt.*;
import cloudstate.samples.chat.friends.grpc.*;

/** Friends service is a CRDT entity that takes care of an OrSet of a users friends. */
@CrdtEntity
public class FriendsEntity {
  private static final Logger logger = LogManager.getLogger(FriendsEntity.class);
  private final ORSet<Friend> friends;

  public FriendsEntity(ORSet<Friend> friends) {
    this.friends = friends;
  }

  @CommandHandler
  public Empty add(FriendRequest req) {
    friends.add(req.getFriend());
    logger.debug("add friend command: {} added {}/{}", req.getUser(), req.getFriend().getUser(),req.getFriend().getAvatar());
    return Empty.getDefaultInstance();
  }

  @CommandHandler
  public FriendsList getFriends(User user) {
    FriendsList.Builder b = FriendsList.newBuilder();
    for(Friend f: friends) {
      b.addFriends(f);
    }
    logger.debug("get friends command: ", user.getUser());
    return b.build();
  }

  @CommandHandler
  public Empty remove(FriendRequest req) {
    friends.remove(req.getFriend());
    logger.debug("remove friend command: {} removed {}/{}", req.getUser(), req.getFriend().getUser(),req.getFriend().getAvatar());
    return Empty.getDefaultInstance();
  }
}
