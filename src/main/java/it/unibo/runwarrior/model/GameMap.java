package it.unibo.runwarrior.model;



import java.awt.image.BufferedImage;
import java.util.Map;



/**

* Represents the game map, holding the grid data and the images for each block type.

* This class is final as it's not designed for extension.

*/

public final class GameMap {



  private final int[][] mapData;

  private final int rows;

  private final int cols;

  private final Map<Integer, BufferedImage> blockImages;



  /**

  * Constructs a new GameMap.

  *

  * @param mapData   the 2D array representing the map grid.

  * @param blockImages a map linking block integer values to their images.

  * @param rows    the number of rows in the map.

  * @param cols    the number of columns in the map.

  */

  public GameMap(final int[][] mapData, final Map<Integer, BufferedImage> blockImages, final int rows, final int cols) {

    this.mapData = mapData.clone();

    this.blockImages = blockImages;

    this.rows = rows;

    this.cols = cols;

  }



  /**

  * Loads a map from data files and image configuration files.

  * This is a factory method to create a GameMap instance.

  *

  * @param mapDataFilePath   the path to the file containing the map's numerical data.

  * @param imageConfigFilePath the path to the file configuring the block images.

  * @return a new GameMap instance, or null if loading fails.

  */

  public static GameMap load(final String mapDataFilePath, final String imageConfigFilePath) {



    System.out.println("Inizio caricamento dati mappa da: " + mapDataFilePath);

    final MapLoader rawMapData = MapLoader.load(mapDataFilePath);

    if (rawMapData == null) {

      System.err.println("GameMap Error: Impossibile caricare i dati numerici della mappa da " + mapDataFilePath);

      return null;

    }

    System.out.println("Dati mappa caricati con successo.");



    System.out.println("Inizio caricamento immagini da config: " + imageConfigFilePath);

    final ImageLoader mapImageLoader = new ImageLoader();

    final boolean imagesLoaded = mapImageLoader.loadImagesFromConfigFile(imageConfigFilePath);

    if (!imagesLoaded) {

      // Ho spezzato la riga per rispettare il limite di lunghezza

      System.err.println("GameMap Error: Impossibile caricare le immagini per la mappa"

        + " utilizzando la configurazione " + imageConfigFilePath);

      return null;

    }

    System.out.println("Immagini caricate con successo.");



    System.out.println("Creazione oggetto GameMap.");

    return new GameMap(

      rawMapData.getMapData(),

      mapImageLoader.getLoadedImages(),

      rawMapData.getRows(),

      rawMapData.getCols()

    );

  }



  /**

  * Returns the map data as a 2D integer array.

  *

  * @return the map grid.

  */

  public int[][] getMapData() {

    return mapData.clone();

  }



  /**

  * Returns the number of rows in the map.

  *

  * @return the number of rows.

  */

  public int getRows() {

    return rows;

  }



  /**

  * Returns the number of columns in the map.

  *

  * @return the number of columns.

  */

  public int getCols() {

    return cols;

  }



  /**

  * Gets the image for a specific block value.

  *

  * @param blockValue the integer value of the block.

  * @return the corresponding BufferedImage.

  */

  public BufferedImage getBlockImage(final int blockValue) {

    return blockImages.get(blockValue);

  }



  /**

  * Returns the map of all block images.

  *

  * @return the map of block values to BufferedImages.

  */

  public Map<Integer, BufferedImage> getBlockImages() {

    return blockImages;

  }

}

