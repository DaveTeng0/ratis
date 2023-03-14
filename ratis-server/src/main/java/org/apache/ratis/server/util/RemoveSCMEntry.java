package org.apache.ratis.server.util;
import org.apache.ratis.server.raftlog.LogProtoUtils;
import org.apache.ratis.server.RaftConfiguration;
import org.apache.ratis.proto.RaftProtos.LogEntryProto;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;



public class RemoveSCMEntry {
    public static void main(String[] args) {
        String dbPath = args[0];
        String peerID = args[1];
        RaftConfiguration raftconf = readRaftConfiguration(peerID, dbPath);
        writeRaftConfiguration(raftconf, dbPath);
    }
   
    
    public static void writeRaftConfiguration(LogEntryProto conf, String dbPath) {
        File confFile = new File(dbPath);
        try (FileOutputStream fio = new FileOutputStream(confFile)) {
          conf.writeTo(fio);
        } catch (Exception e) {
            System.out.println("Failed writing configuration to file:" + confFile + ". Err: " + e.getMessage());
        }
      }
    
      public static RaftConfiguration readRaftConfiguration(String peerID, String dbPath) {
        File confFile = new File(dbPath);
        try (FileInputStream fio = new FileInputStream(confFile)) {
          LogEntryProto confProto = LogEntryProto.newBuilder().mergeFrom(fio).build();
          return LogProtoUtils.toRaftConfiguration(confProto, peerID);
        } catch (FileNotFoundException e) {
          return null;
        } catch (Exception e) {
          System.out.println("Failed reading configuration from file:" + confFile + ". Err: " + e.getMessage());
          return null;
        }
      }

}
