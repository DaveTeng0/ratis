
import org.apache.ratis.server.raftlog.LogProtoUtils;
import org.apache.ratis.server.RaftConfiguration;
import org.apache.ratis.proto.RaftProtos.LogEntryProto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RemoveSCMEntry {

    public static void main(String[] args) {
        String dbPath = args[0];
        String peerHostname = args[1];
        printScreenOldConfFile(dbPath);
        // Read Term, Log Index
        File confFile = new File(dbPath);
        LogEntryProto confProto = null;
        try (FileInputStream fio = new FileInputStream(confFile)) {
          confProto = LogEntryProto.newBuilder().mergeFrom(fio).build();          
        } catch (Exception e) {
          System.out.println("Failed to build conf proto: " + e.getMessage());
        }
        backupConfFile(dbPath, confProto);

        long oriConfTerm = confProto.getTerm();
        long oriConfLogIndex = confProto.getIndex();
        RaftConfiguration raftconf = removeStaleSCMHostInRaftConfiguration(peerHostname, dbPath);
        LogEntryProto newProto =	
          LogProtoUtils.toLogEntryProto(raftconf, oriConfTerm, oriConfLogIndex);
        System.out.println("new proto: " + newProto);

        if (!peerHostname.equals("readonly")) {
          writeRaftConfiguration(newProto, dbPath);
        }
    }
   
    
    public static void writeRaftConfiguration(LogEntryProto conf, String dbPath) {
        File confFile = new File(dbPath);
        try (FileOutputStream fio = new FileOutputStream(confFile)) {
          conf.writeTo(fio);
        } catch (Exception e) {
            System.out.println("Failed to write configuration to file:" + confFile + ". Err: " + e.getMessage());
        }
    }
    
    public static RaftConfiguration removeStaleSCMHostInRaftConfiguration(String peerHostname, String dbPath) {
      File confFile = new File(dbPath);
      try (FileInputStream fio = new FileInputStream(confFile)) {
          LogEntryProto entry = LogEntryProto.newBuilder().mergeFrom(fio).build();

      Preconditions.assertTrue(entry.hasConfigurationEntry())
      final RaftConfigurationProto proto = entry.getConfigurationEntry();
      final List<RaftPeer> conf = ProtoUtils.toRaftPeers(proto.getPeersList());

      List<RaftPeer> filteredConf = conf.stream()
          .filter(p -> !(p.getAddress().contains(filterHostname)))
          .collect(Collectors.toList());

      final List<RaftPeer> oldConf = ProtoUtils.toRaftPeers(proto.getOldPeersList());
      return ServerImplUtils.newRaftConfiguration(filteredConf, entry.getIndex(), oldConf);
      } catch (Exception e) {
          System.out.println("Failed to read configuration from file:" + confFile + ". Err: " + e.getMessage());
          return null;
      }
    }

    public static void printScreenOldConfFile(String filePath) {
      File confFile = new File(filePath);
      try (FileInputStream fio = new FileInputStream(confFile)) {
        LogEntryProto confProto = LogEntryProto.newBuilder().mergeFrom(fio).build();
        System.out.println("old proto: " + confProto);
      } catch (Exception e) {
        System.out.println("Failed to printScreenOldConfFile: " + e.getMessage());
      } 
    }

    public static void backupConfFile(String filePath, LogEntryProto logProto) {
      Path file = Paths.get(
        String.format("%s/%s", "/tmp", "original_raft_meta.conf"));
      if (!Files.exists(file)) {
        try {
          Files.write(file, logProto.toString().getBytes());            
        } catch (Exception e) {
          System.out.println("Failed to backupConfFile: " + e.getMessage());
        }
      }
    }

}



