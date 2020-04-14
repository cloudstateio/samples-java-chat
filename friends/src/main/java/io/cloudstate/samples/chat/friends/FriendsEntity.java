package io.cloudstate.samples.chat.friends;

import io.cloudstate.javasupport.CloudState;
import io.cloudstate.javasupport.EntityId;
import io.cloudstate.javasupport.crdt.*;
import io.cloudstate.samples.chat.friends.FriendsProtos.*;

import java.util.Iterator;
import java.util.Optional;

/**
 * A Friends entity.
 * <p/>
 * There will be at most one of these on each node per user. It holds the CRDT, which is a Vote CRDT, which
 * allows us to register this nodes vote as to whether the user is currently online. The Vote CRDT then tells
 * us how many nodes have voted, and if at least one of them voted true, we know the user is online.
 */
@CrdtEntity
public class FriendsEntity {
    /**
     * The user who this entity represents.
     */
    private final String username;

    /**
     * The ORSet CRDT.
     */
    private final ORSet<Friend> friends;

    /**
     * Constructor. The CloudState java support library makes several different types of parameters available both for
     * injection into the constructor, as well as passing into any CommandHandler. Those are the context, the CRDT, and
     * the entity id, which in this case is the user that this entity is for.
     */
    public FriendsEntity(Optional<ORSet<Friend>> friends, CrdtCreationContext ctx, @EntityId String username) {
        System.out.println("Creating Friends Entity for user " + username);
        this.username = username;
        // If there's an existing CRDT, we use that, otherwise we create a new one.
        this.friends = friends.orElseGet(ctx::newORSet);
    }

    /**
     * Command handlers implementing methods defined in protobuf @friends.proto
     */
    @CommandHandler
    public void add(FriendRequest request, CommandContext ctx) {
        System.out.println("Adding a friend " + request.getFriend().getUser() + " for user " + username);
        friends.add(request.getFriend());
    }

    @CommandHandler
    public void remove(FriendRequest request, CommandContext ctx) {
        System.out.println("Deleting a friend " + request.getFriend().getUser() + " for user " + username);
        friends.remove(request.getFriend());
    }

    @CommandHandler
    public FriendsList getFriends(User user, CommandContext ctx) {
        System.out.println("Getting a list of friends for user " + username);
        Iterator<Friend> iterator = friends.iterator();
        FriendsList.Builder builder = FriendsList.newBuilder();
        while (iterator.hasNext()) {
            builder.addFriends(iterator.next());
        }
        return builder.build();
    }

    /**
     * Main method.
     */
    public static void main(String... args) {
        // Register this entity to handle the Presence service.
        CloudState cloudState = new CloudState().registerCrdtEntity(FriendsEntity.class,
                FriendsProtos.getDescriptor().findServiceByName("Friends"),
                FriendsProtos.getDescriptor()
        );
        cloudState.start();
        System.out.println("Started CRDT service for friends");
    }
}