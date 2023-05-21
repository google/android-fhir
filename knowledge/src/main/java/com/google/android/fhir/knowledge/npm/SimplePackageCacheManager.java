package com.google.android.fhir.knowledge.npm;

import androidx.annotation.Nullable;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.utilities.TextFile;
import org.hl7.fhir.utilities.Utilities;
import org.hl7.fhir.utilities.VersionUtilities;
import org.hl7.fhir.utilities.json.model.JsonObject;
import org.hl7.fhir.utilities.json.parser.JsonParser;
import org.hl7.fhir.utilities.npm.BasePackageCacheManager;
import org.hl7.fhir.utilities.npm.IPackageCacheManager;
import org.hl7.fhir.utilities.npm.NpmPackage;
import org.hl7.fhir.utilities.npm.PackageClient;
import org.hl7.fhir.utilities.npm.PackageServer;
import timber.log.Timber;


/**
 * This is a package cache manager implementation that uses a local disk cache.
 *
 * Simplified version of `FilesystemPackageCacheManager` that
 * supports injected folder instead of hardcoded and use the timber to log.
 */
public class SimplePackageCacheManager extends BasePackageCacheManager implements
    IPackageCacheManager {
  private String cacheFolder;

  public SimplePackageCacheManager(String cacheFolder,
      Function<PackageServer, PackageClient> clientFactory, String... packageServers)
      throws IOException {
    for (String packageServer : packageServers) {
      addPackageServer(new PackageServer(packageServer));
    }

    this.cacheFolder = cacheFolder;
    setClientFactory(clientFactory);
    if (!(new File(cacheFolder).exists())) {
      Utilities.createDirectory(cacheFolder);
    }
  }


  public String getFolder() {
    return cacheFolder;
  }

  private NpmPackage loadPackageInfo(String path) throws IOException {
    return NpmPackage.fromFolder(path);
  }

  private void clearCache() throws IOException {
    for (File f : new File(cacheFolder).listFiles()) {
      if (f.isDirectory()) {
        new CacheLock(f.getName()).doWithLock(() -> {
          Utilities.clearDirectory(f.getAbsolutePath());
          try {
            FileUtils.deleteDirectory(f);
          } catch (Exception e1) {
            try {
              FileUtils.deleteDirectory(f);
            } catch (Exception e2) {
              // just give up
            }
          }
          return null; // must return something
        });
      } else {
        FileUtils.forceDelete(f);
      }
    }
  }

  private void checkValidVersionString(String version, String id) {
    if (Utilities.noString(version)) {
      throw new FHIRException(
          "Cannot add package " + id + " to the package cache - a version must be provided");
    }
    if (version.startsWith("file:")) {
      throw new FHIRException(
          "Cannot add package " + id + " to the package cache - the version '" + version
              + "' is illegal in this context");
    }
    for (char ch : version.toCharArray()) {
      if (!Character.isAlphabetic(ch) && !Character.isDigit(ch) && !Utilities.existsInList(ch, '.',
          '-', '$')) {
        throw new FHIRException(
            "Cannot add package " + id + " to the package cache - the version '" + version
                + "' is illegal (ch '" + ch + "'");
      }
    }
  }

  protected InputStreamWithSrc loadFromPackageServer(String id, String version) {
    InputStreamWithSrc retVal = super.loadFromPackageServer(id, version);
    if (retVal != null) {
      return retVal;
    }

    return super.loadFromPackageServer(id, VersionUtilities.getMajMin(version) + ".x");
  }

  @Override
  public NpmPackage loadPackageFromCacheOnly(String id, @Nullable String version)
      throws IOException {
    return null;
  }

  private NpmPackage loadPackageFromFile(String id, String folder) throws IOException {
    File f = new File(Utilities.path(folder, id));
    if (!f.exists()) {
      throw new FHIRException("Package '" + id + "  not found in folder " + folder);
    }
    if (!f.isDirectory()) {
      throw new FHIRException("File for '" + id + "  found in folder " + folder + ", not a folder");
    }
    File fp = new File(Utilities.path(folder, id, "package", "package.json"));
    if (!fp.exists()) {
      throw new FHIRException("Package '" + id + "  found in folder " + folder
          + ", but does not contain a package.json file in /package");
    }
    return NpmPackage.fromFolder(f.getAbsolutePath());
  }

  /**
   * Clear the cache
   */
  public void clear() throws IOException {
    clearCache();
  }

  // ========================= Utilities ============================================================================

  /**
   * Remove a particular package from the cache
   */
  public void removePackage(String id, String ver) throws IOException {
    new CacheLock(id + "#" + ver).doWithLock(() -> {
      String f = Utilities.path(cacheFolder, id + "#" + ver);
      File ff = new File(f);
      if (ff.exists()) {
        Utilities.clearDirectory(f);
        ff.delete();
      }
      return null;
    });
  }

  /**
   * Load the identified package from the cache - if it exists
   * <p>
   * This is for special purpose only (testing, control over speed of loading). Generally, use the
   * loadPackage method
   */
  public File loadPackageFromCacheOnly1(String id, String version) throws IOException {
    String foundPackage = null;
    String foundVersion = null;
    for (String f : new File(cacheFolder).list()) {
      File cf = new File(Utilities.path(cacheFolder, f));
      if (cf.isDirectory()) {
        if (f.equals(id + "#" + version) || (Utilities.noString(version) && f.startsWith(
            id + "#"))) {
          return new File(Utilities.path(cacheFolder, f));
        }
        if (version != null && !version.equals("current") && (version.endsWith(".x")
            || Utilities.charCount(version, '.') < 2) && f.contains("#")) {
          String[] parts = f.split("#");
          if (parts[0].equals(id) && VersionUtilities.isMajMinOrLaterPatch(
              (foundVersion != null ? foundVersion : version), parts[1])) {
            foundVersion = parts[1];
            foundPackage = f;
          }
        }
      }
    }
    if (foundPackage != null) {
      return new File(Utilities.path(cacheFolder, foundPackage));
    }
    return null;
  }

  /**
   * Add an already fetched package to the cache
   */
  public File addPackageToCacheFile(String id, String version, InputStream packageTgzInputStream,
      String sourceDesc) throws IOException {
    checkValidVersionString(version, id);

    return new CacheLock(id + "#" + version).doWithLock(() -> {
      String packRoot = Utilities.path(cacheFolder, id + "#" + version);
      File packageFolder = new File(packRoot, "package");
      try {
        // ok, now we have a lock on it... check if something created it while we were waiting
        if (!new File(packRoot).exists()) {
          packageFolder.mkdirs();
          TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(packageTgzInputStream));
          TarArchiveEntry currentEntry;
          while ((currentEntry = (TarArchiveEntry) tarInput.getNextEntry()) != null) {
            String currentEntryName = currentEntry.getName();
            if (!currentEntryName.startsWith("package/")){
              continue;
            }
            String fileName = currentEntryName.substring("package/".length());
            File file = new File(packageFolder, fileName);
            byte[] data = new byte[2048];
            OutputStream outStream = new FileOutputStream(file);
            try (BufferedOutputStream dest = new BufferedOutputStream(outStream, 2048)) {
              int count;
              while ((count = tarInput.read(data, 0, 2048)) != -1) {
                dest.write(data, 0, count);
              }
            }
            outStream.close();
          }
        }
      } catch (Exception e) {
        try {
          // don't leave a half extracted package behind
          log("Clean up package " + packRoot + " because installation failed: " + e.getMessage());
          Utilities.clearDirectory(packRoot);
          new File(packRoot).delete();
        } catch (Exception ei) {
          // nothing
        }
        throw e;
      }
      return packageFolder;
    });
  }

  private void log(String s) {
    if (!silent) {
      Timber.d(s);
    }
  }

  @Override
  public String getPackageUrl(String packageId) throws IOException {
    return super.getPackageUrl(packageId);
  }

  @Override
  public NpmPackage loadPackage(String id, String version) throws FHIRException, IOException {
    return null;
  }


  public File loadPackage1(String id, String version) throws FHIRException, IOException {
    File p = loadPackageFromCacheOnly1(id, version);
    if (p != null) {
      return p;
    }
    // nup, don't have it locally (or it's expired)
    InputStreamWithSrc source = loadFromPackageServer(id, version);
    if (source == null) {
      throw new FHIRException("Unable to find package " + id + "#" + version);
    }
    return addPackageToCacheFile(id, source.version, source.stream, source.url);
  }



  @Override
  public String getPackageId(String canonicalUrl) throws IOException {
    String retVal = findCanonicalInLocalCache(canonicalUrl);

    if (retVal == null) {
      retVal = super.getPackageId(canonicalUrl);
    }

    return retVal;
  }

  @Override
  public NpmPackage addPackageToCache(String id, String version, InputStream packageTgzInputStream,
      String sourceDesc) throws IOException {
    return null;
  }


  public String findCanonicalInLocalCache(String canonicalUrl) {
    try {
      for (String pf : listPackages()) {
        if (new File(Utilities.path(cacheFolder, pf, "package", "package.json")).exists()) {
          JsonObject npm = JsonParser.parseObjectFromFile(
              Utilities.path(cacheFolder, pf, "package", "package.json"));
          if (canonicalUrl.equals(npm.asString("canonical"))) {
            return npm.asString("name");
          }
        }
      }
    } catch (IOException e) {
    }
    return null;
  }


  public List<String> listPackages() {
    List<String> res = new ArrayList<>();
    for (File f : new File(cacheFolder).listFiles()) {
      if (f.isDirectory() && f.getName().contains("#")) {
        res.add(f.getName());
      }
    }
    return res;
  }


  public interface CacheLockFunction<T> {

    T get() throws IOException;
  }

  public class CacheLock {

    private final File lockFile;

    public CacheLock(String name) throws IOException {
      this.lockFile = new File(cacheFolder, name + ".lock");
      if (!lockFile.isFile()) {
        TextFile.stringToFile("", lockFile);
      }
    }

    public <T> T doWithLock(CacheLockFunction<T> f) throws IOException {
      try (FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel()) {
        final FileLock fileLock = channel.lock();
        T result = null;
        try {
          result = f.get();
        } finally {
          fileLock.release();
        }
        if (!lockFile.delete()) {
          lockFile.deleteOnExit();
        }
        return result;
      }
    }
  }


  public boolean packageInstalled(String id, String version) {
    for (String f : new File(cacheFolder).list()) {
      if (f.equals(id + "#" + version)) {
        return true;
      }
    }
    return false;
  }


}