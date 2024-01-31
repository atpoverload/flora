package eflect.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.stream.Stream;

/** Simple wrapper to read powercap's energy with pure Java. */
public final class Powercap {
  private static final String POWERCAP_PATH =
      String.join("/", "/sys", "devices", "virtual", "powercap", "intel-rapl");

  public static final int SOCKET_COUNT = getSocketCount();

  /**
   * Returns an {@link PowercapSample} populated by parsing the string returned by {@ readNative}.
   */
  public static double[][] getEnergyStats() {
    if (SOCKET_COUNT == 0) {
      return new double[0][0];
    }
    double[][] sample = new double[SOCKET_COUNT][];

    // pull out energy values
    for (int socket = 0; socket < SOCKET_COUNT; socket++) {
      sample[socket] = new double[2];
      sample[socket][0] = readPackage(socket);
      sample[socket][1] = readDram(socket);
    }

    return sample;
  }

  private static int getSocketCount() {
    try {
      return (int)
          Stream.of(new File(POWERCAP_PATH).list()).filter(f -> f.contains("intel-rapl")).count();
    } catch (Exception e) {
      LoggerUtil.getLogger().fine("couldn't check the socket count; powercap likely not available");
      return 0;
    }
  }

  // TODO: NEED TO LOG THESE!!!!
  /**
   * Parses the contents of /sys/devices/virtual/powercap/intel-rapl/intel-rapl:<socket>/energy_uj,
   * which contains the number of microjoules consumed by the package since boot as an integer.
   */
  private static double readPackage(int socket) {
    String energyFile =
        String.join("/", POWERCAP_PATH, String.format("intel-rapl:%d", socket), "energy_uj");
    try (BufferedReader reader = new BufferedReader(new FileReader(energyFile))) {
      return Double.parseDouble(reader.readLine()) / 1000000;
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * Parses the contents of
   * /sys/devices/virtual/powercap/intel-rapl/intel-rapl:<socket>/intel-rapl:<socket>:0/energy_uj,
   * which contains the number of microjoules consumed by the dram since boot as an integer.
   */
  private static double readDram(int socket) {
    String socketPrefix = String.format("intel-rapl:%d", socket);
    String energyFile =
        String.join(
            "/", POWERCAP_PATH, socketPrefix, String.format("%s:0", socketPrefix), "energy_uj");
    try (BufferedReader reader = new BufferedReader(new FileReader(energyFile))) {
      return Double.parseDouble(reader.readLine()) / 1000000;
    } catch (Exception e) {
      return 0;
    }
  }
}
