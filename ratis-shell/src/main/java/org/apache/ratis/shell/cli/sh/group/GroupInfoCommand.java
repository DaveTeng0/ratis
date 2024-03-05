/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ratis.shell.cli.sh.group;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.ratis.proto.RaftProtos;
import org.apache.ratis.protocol.GroupInfoReply;
import org.apache.ratis.shell.cli.sh.command.AbstractRatisCommand;
import org.apache.ratis.shell.cli.sh.command.Context;

import java.io.IOException;


/**
 * Command for querying ratis group information.
 */
public class GroupInfoCommand extends AbstractRatisCommand {
  /**
   * @param context command context
   */
  public GroupInfoCommand(Context context) {
    super(context);
  }

  @Override
  public String getCommandName() {
    return "info";
  }

  @Override
  public int run(CommandLine cl) throws IOException {
    super.run(cl);
    println("group id: " + getRaftGroup().getGroupId().getUuid());
    final GroupInfoReply reply = getGroupInfoReply();
    RaftProtos.RaftPeerProto leader = getLeader(reply.getRoleInfoProto());
    if (leader == null) {
      println("leader not found");
    } else {
      printf("leader info: %s(%s)%n%n", leader.getId().toStringUtf8(), leader.getAddress());
    }
    println(reply.getPeerInfos());
    return 0;
  }

  @Override
  public String getUsage() {
    return String.format("%s"
        + " -%s <PEER0_HOST:PEER0_PORT,PEER1_HOST:PEER1_PORT,PEER2_HOST:PEER2_PORT>"
        + " [-%s <RAFT_GROUP_ID>]",
        getCommandName(), PEER_OPTION_NAME, GROUPID_OPTION_NAME);
  }

  @Override
  public String getDescription() {
    return description();
  }

  @Override
  public Options getOptions() {
    return super.getOptions();
  }

  /**
   * @return command's description
   */
  public static String description() {
    return "Display the information of a specific raft group";
  }
}
