package io.cloudstate.samples.friends;

import cloudstate.samples.chat.friends.grpc.*;
import io.cloudstate.javasupport.*;

public class Main {

	public static void main(String[] args) throws Exception {

    new CloudState()
        .registerCrdtEntity(
            FriendsEntity.class,
						FriendsGrpc.getDescriptor().findServiceByName("Friends"))
				.start()
        .toCompletableFuture()
        .get();
	}
}
