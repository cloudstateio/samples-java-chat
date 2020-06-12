# CloudState Chat sample - Java services

This is part of the Cloudstate chat sample, which uses CloudState to build the friends management and (online) presence tracking features of a chat system.

These are the only two features, but in future we will add chat room support, push notifications for chat messages, etc.

The application has three components, a presence stateful function, which uses a vote CRDT to store whether a user is currently online or not, a friends service which uses an ORSet CRDT and a gateway that serves a UI. The gateway is written in Node/express, and is shared with the js-chat sample application, source code can be found at http://github.com/cloudstateio/samples-js-chat.

The UI is designed to allow connecting as multiple users in one browser window, this is for demonstration purposes, to make it straight forward to see real time interactions, online presence, friends, serverside pushes etc, without needing to open many browser tabs.

## Building and running

In this repository you will find the presence and friends services implemented using Cloudstate Java support. To build the whole application please see the sample-js-chat repository above.

To build the two services please see the README in the friends and presence directories respectively.
