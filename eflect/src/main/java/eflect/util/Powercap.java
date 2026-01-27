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
  public static final double[][] MAX_ENERGY_JOULES = getMaximumEnergy();

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

  private static double[][] getMaximumEnergy() {
    if (!Files.exists(POWERCAP_ROOT)) {
      logger.warning("couldn't check the maximum energy; powercap likely not available");
      return new double[0][0];
    }
    // TODO: this is a hack and we need to formalize it
    try {
      double[][] maxEnergy =
          Files.list(POWERCAP_ROOT)
              .filter(p -> p.getFileName().toString().contains("intel-rapl"))
              .map(
                  socket -> {
                    double[] overflowValues = new double[2];
                    try {
                      overflowValues[0] =
                          Double.parseDouble(
                                  Files.readString(
                                      Path.of(socket.toString(), "max_energy_range_uj")))
                              / 1000000;
                    } catch (Exception e) {
                      logger.warning(
                          String.format("couldn't check the maximum energy for socket %s", socket));
                    }
                    try {
                      overflowValues[1] =
                          Double.parseDouble(
                                  Files.readString(
                                      Path.of(
                                          socket.toString(),
                                          String.format("%s:0", socket.getFileName()),
                                          "max_energy_range_uj")))
                              / 1000000;
                    } catch (Exception e) {
                      logger.warning(
                          String.format("couldn't check the maximum energy for socket %s", socket));
                    }
                    logger.info(
                        String.format(
                            "retrieved overflow values for %s: %s",
                            socket.getFileName(), Arrays.toString(overflowValues)));
                    return overflowValues;
                  })
              .toArray(double[][]::new);
      return maxEnergy;
    } catch (Exception e) {
      logger.warning("couldn't check the maximum energy; powercap likely not available");
      return new double[0][0];
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
