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

package org.apache.ratis.shell.cli.sh;

import org.apache.ratis.proto.RaftProtos.LogEntryProto;
import org.apache.ratis.proto.RaftProtos.RaftConfigurationProto;
import org.apache.ratis.proto.RaftProtos.RaftPeerProto;


import org.apache.ratis.proto.RaftProtos;
import org.apache.ratis.protocol.RaftPeer;
import org.apache.ratis.server.RaftConfiguration;
import org.apache.ratis.server.impl.RaftServerTestUtil;
import org.apache.ratis.shell.cli.sh.command.Context;
import org.apache.ratis.shell.cli.sh.local.RaftMetaConfCommand;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.ratis.thirdparty.com.google.protobuf.ByteString;
//import org.junit.Before;
//import org.junit.Test;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.io.TempDir;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocalCommandIntegrationTest {

  private Context context;
  private Path tempDir;
  private Path raftMetaConfPath;
  private Path newRaftMetaConfPath;

  private static final String RAFT_META_CONF = "raft-meta.conf";
  private static final String NEW_RAFT_META_CONF = "new-raft-meta.conf";

//  @Before
  public void setup() throws IOException {
//    context = mock(Context.class);
//    tempDir = Paths.get("temp");
//    raftMetaConfPath = tempDir.resolve("raft-meta.conf");
//    newRaftMetaConfPath = tempDir.resolve("new-raft-meta.conf");
//
//    mockStatic(Paths.class);
//    when(Paths.get(any(String.class), any(String.class))).thenCallRealMethod();
//
//    mockStatic(Files.class);
//    when(Files.newInputStream(raftMetaConfPath)).thenReturn(new ByteArrayInputStream("sample content".getBytes()));
//    when(Files.newOutputStream(newRaftMetaConfPath)).thenReturn(new ByteArrayOutputStream());

  }


  void getRaftConf(Path path, int index) throws IOException {
    Map<String, String> map = new HashMap<>();
    map.put("peer1_Id", "host1:9872");
    map.put("peer2_Id", "host2:9872");
    map.put("peer3_Id", "host3:9872");
    map.put("peer4_Id", "host4:9872");
    List<RaftProtos.RaftPeerProto> raftPeerProtos = new ArrayList<>();
    for (Map.Entry<String, String> en : map.entrySet()) {
      raftPeerProtos.add(RaftProtos.RaftPeerProto.newBuilder()
          .setId(ByteString.copyFrom(en.getKey().getBytes(StandardCharsets.UTF_8))).setAddress(en.getValue())
          .setStartupRole(RaftProtos.RaftPeerRole.FOLLOWER).build());
    }

    /// //// ///
    LogEntryProto generateLogEntryProto = LogEntryProto.newBuilder()
        .setConfigurationEntry(RaftConfigurationProto.newBuilder().addAllPeers(raftPeerProtos).build())
        .setIndex(index).build();
    try (OutputStream out = Files.newOutputStream(path)) {
      generateLogEntryProto.writeTo(out);
    }
  }
  @Test
  public void testRunMethod(@TempDir Path tempDir) throws Exception {
    Path output = tempDir
        .resolve(RAFT_META_CONF);
    int index = 1;
    getRaftConf(output, index);

//    Options options = new Options();
//    options.addOption(Option.builder("p").longOpt(RaftMetaConfCommand.PEER_OPTION_NAME).hasArg().build());
//    options.addOption(Option.builder("d").longOpt(RaftMetaConfCommand.PATH_OPTION_NAME).hasArg().build());
//
//    String[] args = {"--peers=peer1|host1:port1,peer2|host2:port2", "--path=" + tempDir.toAbsolutePath().toString()};
//    CommandLine commandLine = new DefaultParser().parse(options, args);

      final StringPrintStream out = new StringPrintStream();
      RatisShell shell = new RatisShell(out.getPrintStream());
      String peersUpdated = "peer1_Id|host1:9872,peer2_id|host2:9872,peer3_id|host3:9872";
      int ret = shell.run("local", "raftMetaConf", "-peers", peersUpdated, "-path", tempDir.toString());
      Assertions.assertEquals(0, ret);

    long indexFromNewConf;
    List<RaftPeerProto> peers;
    // Add additional assertions to verify the contents of the new-raft-meta.conf file
    try (InputStream in = Files.newInputStream(tempDir.resolve(NEW_RAFT_META_CONF))) {
      LogEntryProto logEntry = LogEntryProto.newBuilder().mergeFrom(in).build();
      indexFromNewConf = logEntry.getIndex();
      peers = logEntry.getConfigurationEntry().getPeersList();
    }

    Assertions.assertEquals(index + 1, indexFromNewConf);
    List<String> peersStr = peers.stream()
        .map(peer -> peer.getId().toStringUtf8() + "|" + peer.getAddress())
        .collect(Collectors.toList());

    StringBuilder tmp = new StringBuilder();
    peers.stream().forEach(peer ->
        tmp.append(peer.getId().toStringUtf8()).append("|").append(peer.getAddress()).append(","));
    tmp.deleteCharAt(tmp.length() - 1); // delete last comma

    Assertions.assertEquals(peersUpdated, tmp.toString());
  }


//  final StringPrintStream out = new StringPrintStream();
//  RatisShell shell = new RatisShell(out.getPrintStream());
//  int ret = shell.run("group", "list", "-peers", address, "-peerId",
//      leader.getPeer().getId().toString());
//    Assertions.assertEquals(0, ret);

}
