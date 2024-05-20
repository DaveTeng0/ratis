package org.apache.ratis.shell.cli.sh.command;

import org.apache.commons.cli.CommandLine;
import org.apache.ratis.client.RaftClient;
import org.apache.ratis.protocol.*;
import org.apache.ratis.protocol.exceptions.RaftException;
import org.apache.ratis.shell.cli.RaftUtils;
import org.apache.ratis.util.ProtoUtils;
import org.apache.ratis.util.function.CheckedFunction;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandUtils {
//  private PrintStream printStream;

  public static final RaftGroupId DEFAULT_RAFT_GROUP_ID = RaftGroupId.randomId();

  /**
   * Execute a given function with input parameter from the members of a list.
   *
   * @param list the input parameters
   * @param function the function to be executed
   * @param <T> parameter type
   * @param <K> return value type
   * @param <E> the exception type thrown by the given function.
   * @return the value returned by the given function.
   */
  public static <T, K, E extends Throwable> K runFunction(Collection<T> list, CheckedFunction<T, K, E> function) {
    for (T t : list) {
      try {
        K ret = function.apply(t);
        if (ret != null) {
          return ret;
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
    return null;
  }

//  public static int run(String peersStr, String groupId) throws IOException {
//    List<InetSocketAddress> addresses = new ArrayList<>();
//    String[] peersArray = peersStr.split(",");
//    for (String peer : peersArray) {
//      addresses.add(parseInetSocketAddress(peer));
//    }
//
//    final RaftGroupId raftGroupIdFromConfig = (!groupId.trim().equals(""))?
//        RaftGroupId.valueOf(UUID.fromString(groupId.trim()))
//        : DEFAULT_RAFT_GROUP_ID;
//
//    List<RaftPeer> peers = addresses.stream()
//        .map(addr -> RaftPeer.newBuilder()
//            .setId(RaftUtils.getPeerId(addr))
//            .setAddress(addr)
//            .build()
//        ).collect(Collectors.toList());
//    RaftGroup raftGroup = RaftGroup.valueOf(raftGroupIdFromConfig, peers);
//    try (final RaftClient client = RaftUtils.createClient(raftGroup)) {
//      final RaftGroupId remoteGroupId;
//      if (raftGroupIdFromConfig != DEFAULT_RAFT_GROUP_ID) {
//        remoteGroupId = raftGroupIdFromConfig;
//      } else {
//        final List<RaftGroupId> groupIds = run(peers,
//            p -> client.getGroupManagementApi((p.getId())).list().getGroupIds());
//
//        if (groupIds == null) {
//          println("Failed to get group ID from " + peers);
//          return -1;
//        } else if (groupIds.size() == 1) {
//          remoteGroupId = groupIds.get(0);
//        } else {
//          println("There are more than one groups, you should specific one. " + groupIds);
//          return -2;
//        }
//      }
//
//      groupInfoReply = run(peers, p -> client.getGroupManagementApi((p.getId())).info(remoteGroupId));
//      processReply(groupInfoReply,
//          () -> "Failed to get group info for group id " + remoteGroupId.getUuid() + " from " + peers);
//      raftGroup = groupInfoReply.getGroup();
//    }
//    return 0;
//  }

}
