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
package org.apache.ratis.protocol;

import org.apache.ratis.proto.RaftProtos;

import java.util.Collection;
import java.util.List;

public class PeerInfoReply extends RaftClientReply {

  private RaftGroup group;
  private RaftProtos.RoleInfoProto roleInfoProto;
  private long currentTerm;
  private long lastCommitIndex;
  private long lastAppliedIndex;
  private List<Long> followerNextIndex;
  private long lastSnapshotIndex;

  @SuppressWarnings("parameternumber")
  public PeerInfoReply(RaftClientRequest request,
                       RaftGroup group,
                       RaftProtos.RoleInfoProto roleInfoProto,
                       long currentTerm,
                       long lastCommitIndex,
                       long lastAppliedIndex,
                       List<Long> followerNextIndex,
                       long lastSnapshotIndex
  ) {
    this(request.getClientId(),
        request.getServerId(),
        request.getRaftGroupId(),
        request.getCallId(),
        group,
        roleInfoProto,
        currentTerm,
        lastCommitIndex,
        lastAppliedIndex,
        followerNextIndex,
        lastSnapshotIndex);

  }

  @SuppressWarnings("parameternumber")
  public PeerInfoReply(ClientId clientId,
                       RaftPeerId serverId,
                       RaftGroupId groupId,
                       long callId,
                       RaftGroup group,
                       RaftProtos.RoleInfoProto roleInfoProto,
                       long currentTerm,
                       long lastCommitIndex,
                       long lastAppliedIndex,
                       List<Long> followerNextIndex,
                       long lastSnapshotIndex) {
    super(clientId, serverId, groupId, callId, true, null, null, 0L, null);
    this.group = group;
    this.roleInfoProto = roleInfoProto;
    this.currentTerm = currentTerm;
    this.lastCommitIndex = lastCommitIndex;
    this.lastAppliedIndex = lastAppliedIndex;
    this.followerNextIndex = followerNextIndex;
    this.lastSnapshotIndex = lastSnapshotIndex;
  }

  public RaftProtos.RoleInfoProto getRoleInfoProto() {
    return roleInfoProto;
  }

  public RaftGroup getGroup() {
    return group;
  }

  public long getCurrentTerm() {
    return currentTerm;
  }

  public long getLastCommitIndex() {
    return lastCommitIndex;
  }

  public long getLastAppliedIndex() {
    return lastAppliedIndex;
  }

  public List<Long> getFollowerNextIndex() {
    return followerNextIndex;
  }

  public long getLastSnapshotIndex() {
    return lastSnapshotIndex;
  }

}
