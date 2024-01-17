package eflect.util;

import java.util.Arrays;
import java.util.logging.Logger;

/** Class that provides a static interface to RAPL measurements. */
public final class Rapl {
  private static Rapl instance;

  public static Rapl getInstance() {
    return getInstance(null);
  }

  public static synchronized Rapl getInstance(String path) {
    if (instance != null) {
      return instance;
    }

    if (path == null) {
      try {
        System.loadLibrary("CPUScaler");
      } catch (UnsatisfiedLinkError e) {
        LoggerUtil.getLogger().fine("couldn't find CPUScaler!");
        return null;
      } catch (Exception e) {
        LoggerUtil.getLogger().fine("couldn't find CPUScaler!");
        return null;
      }
    } else {
      System.load(path);
    }

    instance = new Rapl();
    return instance;
  }

  private native int ProfileInit();

  private native int GetSocketNum();

  private native String EnergyStatCheck();

  private final int socketCount;
  private final double wrapAroundEnergy;

  private Rapl() {
    wrapAroundEnergy = ProfileInit();
    socketCount = GetSocketNum();
  }

  /**
   * @return an array of arrays of the current energy information by socket.
   *     <p>subarray structure is architecture dependent. Typically, 0 -> dram, 1 -> cpu, 2 ->
   *     package.
   */
  public synchronized double[][] getEnergyStats() {
    // guard if CPUScaler isn't available
    if (socketCount < 0) {
      return new double[0][0];
    }
    String EnergyInfo = EnergyStatCheck();
    if (socketCount == 1) {
      /*One Socket*/
      double[][] stats = new double[1][3];
      String[] energy = EnergyInfo.split("#");

      stats[0][0] = Double.parseDouble(energy[0]);
      stats[0][1] = Double.parseDouble(energy[1]);
      stats[0][2] = Double.parseDouble(energy[2]);

      return stats;
    } else {
      /*Multiple sockets*/
      String[] perSockEner = EnergyInfo.split("@");
      double[][] stats = new double[socketCount][3];

      for (int i = 0; i < perSockEner.length; i++) {
        String[] energy = perSockEner[i].split("#");
        for (int j = 0; j < energy.length; j++) {
          stats[i][j] = Double.parseDouble(energy[j]);
        }
      }

      return stats;
    }
  }

  public int getSocketCount() {
    return socketCount;
  }

  public double getWrapAroundEnergy() {
    return wrapAroundEnergy;
  }

  public static void main(String[] args) {
    Logger logger = LoggerUtil.getLogger();
    for (double[] socketEnergy : getInstance().getEnergyStats()) {
      logger.info(String.format("%s", Arrays.toString(socketEnergy)));
    }
  }
}
